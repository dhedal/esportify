import { FetchUtils } from "../utils/fetch-utils.js";
import { MessageUtils} from "../utils/message-utils.js";

/**
 *
 */
class AdminMenu {
    dashboardSectionBtn;
    eventSectionBtn;
    userSectionBtn;

    dashboardSection;
    eventSection;
    userSection;

    isDashboardLoaded;
    isEventsLoaded;
    isUsersLoaded;

    constructor(content = "admin-sidebar") {
        this.dashboardSectionBtn = document.getElementById("dashboard-section-btn");
        this.dashboardSection = document.getElementById("dashboard-section");
        this.dashboardSectionBtn.addEventListener("click", () => this.showDashboardSection());

        this.eventSectionBtn = document.getElementById("event-section-btn");
        this.eventSection = document.getElementById("event-section");
        this.eventSectionBtn.addEventListener("click", () => this.showEventSection());

        this.userSectionBtn = document.getElementById("user-section-btn");
        this.userSection = document.getElementById("user-section");
        this.userSectionBtn.addEventListener("click", () => this.showUserSection());

        this.isDashboardLoaded = false;
        this.isEventsLoaded = false;
        this.isUsersLoaded = false;
    }

    showSection(section) {
        if(!section) return;
        this.dashboardSection.style.display = "none"
        this.eventSection.style.display = "none";
        this.userSection.style.display = "none";
        section.style.display = "block";
    }

    showDashboardSection() {
        this.showSection(this.dashboardSection);

        if(this.isDashboardLoaded) return;
        loadDashboard().then(() => {
            this.isDashboardLoaded = true;
        });
    }

    showEventSection() {
        this.showSection(this.eventSection);

        if(this.isEventsLoaded) return;
        loadEvents().then(() => {
            this.isEventsLoaded = true;
        });
    }

    showUserSection() {
        this.showSection(this.userSection);

        if(this.isUsersLoaded) return;
        loadUsers().then(() => {
            this.isUsersLoaded = true;
        });
    }
}

// Charger les statistiques du tableau de bord
async function loadDashboard() {
    const stats = await FetchUtils.fetch(FetchUtils.ADMIN_API_URL + "/stats");

    if (!stats || stats.error) {
        console.error("Erreur de chargement des statistiques");
        return;
    }

    document.getElementById("pendingEventsCount").innerText = stats.pendingEvents || 0;
    document.getElementById("usersCount").innerText = stats.totalUsers || 0;
    document.getElementById("activeParticipantsCount").innerText = stats.activeParticipants || 0;
}

// Charger les événements en attente de validation
async function loadEvents() {
    const events = await FetchUtils.fetch(FetchUtils.ADMIN_API_URL + "/events");

    const eventsTable = document.getElementById("eventsTable");
    eventsTable.innerHTML = "";

    if (!events || events.error) {
        eventsTable.innerHTML = `<tr><td colspan="5" class="text-center text-danger">Aucun événement trouvé</td></tr>`;
        return;
    }

    events.forEach(event => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${event.title}</td>
            <td>${event.organizer.pseudo}</td>
            <td>${new Date(event.startDateTime).toLocaleString()}</td>
            <td>${event.status}</td>
            <td>
                <div class="btn-group">
                    <button class="btn btn-success btn-sm btn-validate" data-event-id="${event.uuid}">Valider</button>
                    <button class="btn btn-danger btn-sm btn-suspend" data-event-id="${event.uuid}">Suspendre</button>
                </div>
            </td>
        `;
        eventsTable.appendChild(row);
    });

    document.querySelectorAll(".btn-validate").forEach(button => {
        button.addEventListener("click", async (event) => {
            const eventId = event.target.getAttribute("data-event-id");
            await updateEventStatus(eventId, "VALIDATED");
        });
    });

    document.querySelectorAll(".btn-suspend").forEach(button => {
        button.addEventListener("click", async (event) => {
            const eventId = event.target.getAttribute("data-event-id");
            await updateEventStatus(eventId, "SUSPENDED");
        });
    });
}

// Mettre à jour le statut d’un événement (Valider/Suspendre)
async function updateEventStatus(eventId, newStatus) {
    const response = await FetchUtils.fetch(`${FetchUtils.ADMIN_API_URL}/events/status`, "PUT", {
        uuid: eventId,
        status: newStatus
    });

    if (response.ok) {
        MessageUtils.success(`Événement ${newStatus === "VALIDATED" ? "validé" : "suspendu"} avec succès.`, "success");
        loadEvents();
    } else {
        MessageUtils.danger("Erreur lors de la mise à jour de l'événement.");
    }
}

// Charger les utilisateurs et gérer les rôles
async function loadUsers() {
    const users = await FetchUtils.fetch(FetchUtils.ADMIN_API_URL + "/users");

    const usersTable = document.getElementById("usersTable");
    usersTable.innerHTML = "";

    if (!users || users.error) {
        usersTable.innerHTML = `<tr><td colspan="4" class="text-center text-danger">Aucun utilisateur trouvé</td></tr>`;
        return;
    }

    users.forEach(user => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${user.pseudo}</td>
            <td>${user.email}</td>
            <td>
                <select class="form-select user-role" data-user-id="${user.uuid}">
                    <option value="VISITOR" ${user.status === "VISITOR" ? "selected" : ""}>Visiteur</option>
                    <option value="PLAYER" ${user.status === "PLAYER" ? "selected" : ""}>Joueur</option>
                    <option value="ORGANIZER" ${user.status === "ORGANIZER" ? "selected" : ""}>Organisateur</option>
                    <option value="ADMIN" ${user.status === "ADMIN" ? "selected" : ""}>Administrateur</option>
                </select>
            </td>
        `;
        usersTable.appendChild(row);
    });

    document.querySelectorAll(".user-role").forEach(select => {
        select.addEventListener("change", async (event) => {
            const userId = event.target.getAttribute("data-user-id");
            const newRole = event.target.value;
            await updateUserRole(userId, newRole);
        });
    });
}

// Mettre à jour le rôle d'un utilisateur
async function updateUserRole(userId, newRole) {
    const response = await FetchUtils.fetch(`${FetchUtils.ADMIN_API_URL}/users/${userId}/role`, "PUT", {
        role: newRole
    });

    if (response.ok) {
        MessageUtils.success("Rôle mis à jour avec succès.");
    } else {
        MessageUtils.danger("Erreur lors de la mise à jour du rôle.");
    }
}

// Charger toutes les données au chargement de la page
document.addEventListener("DOMContentLoaded", () => {
    const menu = new AdminMenu();
});










// // Charger les utilisateurs
// const loadUsers = async () => {
//
//     const users = await FetchUtils.fetch(`${FetchUtils.ADMIN_API_URL}/users`);
//
//     const usersTable = document.getElementById("usersTable");
//     usersTable.innerHTML = "";
//
//     if (!users || users.error) {
//         usersTable.innerHTML = `<tr><td colspan="4" class="text-center text-danger">Aucun utilisateur trouvé</td></tr>`;
//         return;
//     }
//
//     users.forEach(user => {
//         const row = document.createElement("tr");
//         row.innerHTML = `
//             <td>${user.pseudo}</td>
//             <td>${user.email}</td>
//             <td>${user.status}</td>
//             <td>${new Date(user.createdAt).toLocaleDateString()}</td>
//         `;
//         usersTable.appendChild(row);
//     });
// }
//
// // Filtrer les utilisateurs
// const filterUsers = () => {
//     const searchQuery = document.getElementById("searchUser").value.toLowerCase();
//     const selectedRole = document.getElementById("filterRole").value;
//
//     document.querySelectorAll("#usersTable tr").forEach(row => {
//         const name = row.children[0].innerText.toLowerCase();
//         const role = row.children[2].innerText;
//
//         const matchesSearch = name.includes(searchQuery);
//         const matchesRole = selectedRole === "" || role === selectedRole;
//
//         row.style.display = matchesSearch && matchesRole ? "" : "none";
//     });
// }
//
// // Événements sur les filtres
// document.getElementById("searchUser").addEventListener("input", filterUsers);
// document.getElementById("filterRole").addEventListener("change", filterUsers);
//
// // Charger les utilisateurs au chargement de la page
// document.addEventListener("DOMContentLoaded", loadUsers);
