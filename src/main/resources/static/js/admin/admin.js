import { FetchUtils } from "../utils/fetch-utils.js";
import { MessageUtils} from "../utils/message-utils.js";

let eventsPagination;
let  usersPagination;
let selectedUserUuid = null;
let selectedUserStatus = null;
let confirmModal;

const showRoleChangeConfirmation = (userUuid, statusKey) => {
    selectedUserUuid = userUuid;
    selectedUserStatus = UserStatus.getByKey(statusKey);

    document.getElementById("newStatusText").innerText = selectedUserStatus.label;
    if(confirmModal) confirmModal.show();
}


class UserStatus {
    static VISITOR = {key: 0, label: "Visiteur"};
    static PLAYER = {key: 1, label: "Joueur"};
    static ORGANIZER = {key: 2, label: "Organisateur"};
    static ADMIN = {key: 3, label: "Administrateur"};

    static getByKey(key) {
        switch(key) {
            case 1 : return UserStatus.PLAYER;
            case 2 : return UserStatus.ORGANIZER;
            case 3 : return UserStatus.ADMIN;
            default: return UserStatus.VISITOR;
        }
    }

}

/**
 *
 */
class Pagination {
    constructor(containerId, onPageChange) {
        this.container = document.getElementById(containerId);
        this.onPageChange = onPageChange;
        this.currentPage = 1;
        this.totalPages = 1;
    }

    updatePagination(totalPages) {
        this.totalPages = totalPages;
        this.render();
    }

    render() {
        this.container.innerHTML = "";
        if (this.totalPages <= 1) return;

        let prevDisabled = this.currentPage === 1 ? "disabled" : "";
        let nextDisabled = this.currentPage === this.totalPages ? "disabled" : "";

        this.container.innerHTML = `
            <li class="page-item ${prevDisabled}">
                <a class="page-link" href="#" data-page="${this.currentPage - 1}">Précédent</a>
            </li>
            ${Array.from({ length: this.totalPages }, (_, i) => `
                <li class="page-item ${i + 1 === this.currentPage ? "active" : ""}">
                    <a class="page-link" href="#" data-page="${i + 1}">${i + 1}</a>
                </li>
            `).join('')}
            <li class="page-item ${nextDisabled}">
                <a class="page-link" href="#" data-page="${this.currentPage + 1}">Suivant</a>
            </li>
        `;

        this.container.querySelectorAll(".page-link").forEach(link => {
            link.addEventListener("click", (event) => {
                event.preventDefault();
                let page = parseInt(event.target.getAttribute("data-page"));
                if (page >= 1 && page <= this.totalPages) {
                    this.currentPage = page;
                    this.onPageChange(page);
                    this.render();
                }
            });
        });
    }
}

/**
 *
 */
class AdminMenu {
    dashboardSectionBtn;
    eventSectionBtn;
    userSectionBtn;

    sections;

    isDashboardLoaded;
    isEventsLoaded;
    isUsersLoaded;



    constructor(containerId = "admin-sidebar") {
        this.sections = {
            dashboard: document.getElementById("dashboard-section"),
            event: document.getElementById("event-section"),
            user: document.getElementById("user-section")
        };

        this.dashboardSectionBtn = document.getElementById("dashboard-section-btn");
        this.eventSectionBtn = document.getElementById("event-section-btn");
        this.userSectionBtn = document.getElementById("user-section-btn");

        this.dashboardSectionBtn.addEventListener("click", () => this.showDashboardSection());
        this.eventSectionBtn.addEventListener("click", () => this.showEventSection());
        this.userSectionBtn.addEventListener("click", () => this.showUserSection());

        this.isDashboardLoaded = false;
        this.isEventsLoaded = false;
        this.isUsersLoaded = false;
    }

    showSection(name) {
        if(!name) return;
        Object.values(this.sections).forEach(section => section.style.display = "none");
        this.sections[name].style.display = "block";
    }

    showDashboardSection() {
        this.showSection("dashboard");

        if(this.isDashboardLoaded) return;
        loadDashboard().then(() => {
            this.isDashboardLoaded = true;
        });
    }

    showEventSection() {
        this.showSection("event");

        if(this.isEventsLoaded) return;

        if(!eventsPagination) eventsPagination = new Pagination("events-pagination", loadEvents);

        this.isEventsLoaded = true;
    }

    showUserSection() {
        this.showSection("user");

        if(this.isUsersLoaded) return;

        if(!usersPagination) usersPagination = new Pagination("users-pagination", loadUsers);

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

// Charger les événements avec pagination
const loadEvents = async (page = 1) => {

    const events = await FetchUtils.fetch(`${FetchUtils.ADMIN_API_URL}/events?page=${page}`);

    const eventsTable = document.getElementById("events-table");
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
async function loadUsers(page = 1) {
    const searchQuery = document.getElementById("searchUserInput").value.trim();
    const selectedStatus = document.getElementById("filterStatus").value;

    let url = `${FetchUtils.ADMIN_API_URL}/users?page=${page}`;
    if(searchQuery) url += `&search=${searchQuery}`;
    if(selectedStatus) url += `&status=${selectedStatus}`;

    const response = await FetchUtils.fetch(url);
    const {users, totalPages } = response;

    const usersTable = document.getElementById("users-table");
    usersTable.innerHTML = "";

    if (!users || users.length === 0) {
        usersTable.innerHTML = `<tr><td colspan="4" class="text-center text-danger">Aucun utilisateur trouvé</td></tr>`;
        return;
    }

    users.forEach(user => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${user.pseudo}</td>
            <td>${user.email}</td>
            <td>
                <select class="form-select user-status" data-user-id="${user.uuid}">
                    <option value="${UserStatus.VISITOR.key}" ${user.status.key === UserStatus.VISITOR.key ? "selected" : ""}>${UserStatus.VISITOR.label}</option>
                    <option value=${UserStatus.PLAYER.key}" ${user.status.key === UserStatus.PLAYER.key ? "selected" : ""}>${UserStatus.PLAYER.label}</option>
                    <option value=${UserStatus.ORGANIZER.key}" ${user.status.key === UserStatus.ORGANIZER.key ? "selected" : ""}>${UserStatus.ORGANIZER.label}</option>
                    <option value=${UserStatus.ADMIN.key}" ${user.status.key === UserStatus.ADMIN.key ? "selected" : ""}>${UserStatus.ADMIN.label}</option>
                </select>
            </td>
        `;
        usersTable.appendChild(row);
        usersPagination.updatePagination(totalPages)
    });


    document.querySelectorAll(".user-status").forEach(select => {
        select.addEventListener("change", async (event) => {
            const userId = event.target.getAttribute("data-user-id");
            const statusKey = event.target.value;
            showRoleChangeConfirmation(userId, parseInt(statusKey));
        });
    });
}

// Mettre à jour le status d'un utilisateur
async function updateUserStatus(userId, statusKey) {

    const data = {userUuid: selectedUserUuid, statusKey: selectedUserStatus.key};
    const response = await FetchUtils.fetch(`${FetchUtils.ADMIN_API_URL}/user-status`, "PUT", data);

    if (response.ok) {
        MessageUtils.success("Status mis à jour avec succès.");
    } else {
        const messages = Array.from(response.messages);
        messages.forEach(message => {
            MessageUtils.danger(message);
        });
    }
}

// Charger toutes les données au chargement de la page
document.addEventListener("DOMContentLoaded", () => {
    const menu = new AdminMenu();

    confirmModal = new window.bootstrap.Modal(document.getElementById("confirmStatusChangeModal"));

    const confirmStatusChangeBtn = document.getElementById("confirm-status-change-btn");
    if(confirmStatusChangeBtn) {
        confirmStatusChangeBtn.addEventListener("click", () => {
            updateUserStatus().then(() => {
                selectedUserUuid = null;
                selectedUserStatus = null;
                if(confirmModal) confirmModal.hide();
            })
        });
    }

    const searchUserBtn = document.getElementById("search-user-btn");
    if(searchUserBtn) {
        searchUserBtn.addEventListener("click", () => loadUsers());
    }
});
