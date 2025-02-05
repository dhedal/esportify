import {FetchUtils} from "../utils/fetch-utils.js";

const loadEvents = async () => {
    const events = await FetchUtils.fetch(FetchUtils.EVENT_API_URL);

    const eventsTable = document.getElementById("eventsTable");
    eventsTable.innerHTML = "";

    if (!events || events.error) {
        eventsTable.innerHTML = `<tr><td colspan="5" class="text-center text-danger">Aucun événement disponible</td></tr>`;
        return;
    }

    events.forEach(event => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${event.title}</td>
            <td>${new Date(event.startDateTime).toLocaleDateString()}</td>
            <td>${event.organizer.pseudo}</td>
            <td>${event.maxPlayers}</td>
            <td>${event.status.label}</td>
            <td><a href="/events/${event.id}" class="btn btn-info">Détails</a></td>
        `;
        eventsTable.appendChild(row);
    });
};

document.addEventListener("DOMContentLoaded", loadEvents);