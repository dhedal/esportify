import { FetchUtils } from "../utils/fetch-utils.js";
import { MessageUtils } from "../utils/message-utils.js";
import {Pagination} from "../utils/pagination-utils.js";

let lastEventsRequest;
let eventsPagination;
let organizerInput;
let organizerDropdown;
let organizerDatalist;
let allOrganizers = [];

/**
 * Charger la liste des événements avec filtres et pagination
 */
const loadEvents = async (page = 1, request = null) => {
    if (!request) {
        const searchQuery = document.getElementById("searchEventInput").value.trim();
        const selectedPlayers = document.getElementById("filterPlayers").value;
        const selectedDate = document.getElementById("filterDate").value;
        const selectedOrganizer = document.getElementById("filterOrganizer").value.trim();

        request = `${FetchUtils.EVENT_API_URL}/filter?page=${page}`;

        if (searchQuery !== "") request += `&search=${encodeURIComponent(searchQuery)}`;
        if (selectedPlayers !== "") request += `&players=${encodeURIComponent(selectedPlayers)}`;
        if (selectedDate !== "") request += `&date=${encodeURIComponent(selectedDate)}`;
        if (selectedOrganizer !== "") request += `&organizer=${encodeURIComponent(selectedOrganizer)}`;
    }

    const response = await FetchUtils.fetch(request);
    const { events, totalPages } = response;
    lastEventsRequest = request;

    const eventsTable = document.getElementById("eventsTable");
    eventsTable.innerHTML = "";

    if (!events || events.length === 0) {
        eventsTable.innerHTML = `<tr><td colspan="6" class="text-center text-danger">Aucun événement trouvé</td></tr>`;
        return;
    }

    events.forEach(event => {
        const isRegistered = event.isRegistered !== undefined && event.isRegistered;

        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${event.title}</td>
            <td>${new Date(event.startDateTime).toLocaleDateString()}</td>
            <td>${event.organizer.pseudo}</td>
            <td>${event.maxPlayers}</td>
            <td>${event.status.label}</td>
            <td>
                <a href="/events/detail/${event.uuid}" class="btn btn-link">Consulter</a>
            </td>
        `;
        eventsTable.appendChild(row);
    });

    // Attacher les événements sur les boutons d'inscription/désinscription
    document.querySelectorAll(".register-btn").forEach(button => {
        button.addEventListener("click", async (event) => {
            const eventId = event.target.getAttribute("data-event-id");
            await registerToEvent(eventId);
        });
    });

    document.querySelectorAll(".unregister-btn").forEach(button => {
        button.addEventListener("click", async (event) => {
            const eventId = event.target.getAttribute("data-event-id");
            await unregisterFromEvent(eventId);
        });
    });

    // Mise à jour de la pagination après chargement des événements
    eventsPagination.updatePagination(totalPages);
};


/**
 * Charger tous les organisateurs au chargement de la page
 */
const loadOrganizerList = async () => {
    const organizers = await FetchUtils.fetch(`${FetchUtils.EVENT_API_URL}/organizers`);
    organizerDatalist.innerHTML = ""; // Nettoyer les anciennes suggestions
    allOrganizers = organizers || [];

    if (allOrganizers.length === 0) return;

    allOrganizers.forEach(organizer => {
        const option = document.createElement("option");
        option.value = organizer.pseudo;
        organizerDatalist.appendChild(option);
    });
};

/**
 * Filtrer la liste des organisateurs en fonction de la saisie utilisateur
 */
const filterOrganizerList = () => {
    const query = organizerInput.value.toLowerCase().trim();
    const filteredOrganizers = allOrganizers.filter(org => org.pseudo.toLowerCase().includes(query));

    organizerDatalist.innerHTML = ""; // Nettoyer la liste

    filteredOrganizers.forEach(organizer => {
        const option = document.createElement("option");
        option.value = organizer.pseudo;
        organizerDatalist.appendChild(option);
    });
};

/**
 * Afficher la liste filtrée sous forme de dropdown
 */
const showOrganizerDropdown = () => {
    const query = organizerInput.value.toLowerCase().trim();
    const filteredOrganizers = allOrganizers.filter(org => org.pseudo.toLowerCase().includes(query));

    organizerDropdown.innerHTML = ""; // Nettoyer la liste
    if (filteredOrganizers.length === 0) {
        organizerDropdown.style.display = "none";
        return;
    }

    filteredOrganizers.forEach(organizer => {
        const item = document.createElement("a");
        item.classList.add("dropdown-item");
        item.textContent = organizer.pseudo;
        item.href = "#";

        // ✅ Remplir l'input lorsqu'on sélectionne un élément
        item.addEventListener("click", (event) => {
            event.preventDefault();
            organizerInput.value = organizer.pseudo;
            organizerDropdown.style.display = "none"; // Masquer la liste après sélection
        });

        organizerDropdown.appendChild(item);
    });

    organizerDropdown.style.display = "block"; // Afficher la liste
};





// Chargement initial des événements
document.addEventListener("DOMContentLoaded", () => {
    organizerInput = document.getElementById("filterOrganizer");
    organizerDatalist = document.getElementById("organizerList");
    organizerDropdown = document.getElementById("organizerDropdown");

    organizerInput.addEventListener("keyup", filterOrganizerList);
    organizerInput.addEventListener("keyup", showOrganizerDropdown);

    // Cacher le dropdown lorsqu'on clique ailleurs
    document.addEventListener("click", (event) => {
        if (!organizerInput.contains(event.target) && !organizerDropdown.contains(event.target)) {
            organizerDropdown.style.display = "none";
        }
    });

    eventsPagination = new Pagination("eventsPagination", loadEvents);

    const searchEventBtn = document.getElementById("searchEventBtn");
    if(searchEventBtn) {
        searchEventBtn.addEventListener("click", () => {
            loadEvents(eventsPagination.currentPage).then();
        });
    }

    loadOrganizerList().then();




    loadEvents().then();
});
