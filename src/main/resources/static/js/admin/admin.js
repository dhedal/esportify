import { FetchUtils } from "../utils/fetch-utils.js";
import { MessageUtils} from "../utils/message-utils.js";
import { EventStatus, UserStatus, AskStatus} from "../utils/data-utils.js";
import { Pagination} from "../utils/pagination-utils.js";


let  usersPagination;
let selectedUserUuid = null;
let selectedUserStatus = null;
let confirmUserModal;
let lastUserRequest = null;

let eventsPagination;
let selectedEventUuid = null;
let selectedEventStatus = null;
let confirmEventModal;
let lastEventRequest = null;

let asksPagination;
let selectedAskUuid = null;
let selectedAskStatus = null;
let confirmAskModal;
let lastAskRequest = null;



/**
 *
 * @param userUuid
 * @param statusKey
 */
const showUserStatusChangeConfirmation = (userUuid, statusKey) => {
    selectedUserUuid = userUuid;
    selectedUserStatus = UserStatus.getByKey(statusKey);

    document.getElementById("new-user-status-label").innerText = selectedUserStatus.label;
    if(confirmUserModal) confirmUserModal.show();
}

/**
 *
 * @param eventUuid
 * @param statusKey
 */
const showEventStatusChangeConfirmation = (eventUuid, statusKey) => {
    selectedEventUuid = eventUuid;
    selectedEventStatus = EventStatus.getByKey(statusKey);

    document.getElementById("new-event-status-label").innerText = selectedEventStatus.label;
    if(confirmEventModal) confirmEventModal.show();
}

/**
 *
 * @param askUuid
 * @param statusKey
 */
const showAskStatusChangeConfirmation = (askUuid, statusKey) => {
    selectedAskUuid = askUuid;
    selectedAskStatus = AskStatus.getByKey(statusKey);
    document.getElementById("new-ask-status-label").innerText = selectedAskStatus.label;
    if(confirmAskModal) confirmAskModal.show();
}




/**
 *
 */
class AdminMenu {
    dashboardSectionBtn;
    eventSectionBtn;
    userSectionBtn;
    askSectionBtn;

    sections;

    isDashboardLoaded;
    isEventsLoaded;
    isUsersLoaded;

    constructor(containerId = "admin-sidebar") {
        this.sections = {
            dashboard: document.getElementById("dashboard-section"),
            event: document.getElementById("event-section"),
            user: document.getElementById("user-section"),
            ask: document.getElementById("ask-section")
        };

        this.dashboardSectionBtn = document.getElementById("dashboard-section-btn");
        this.eventSectionBtn = document.getElementById("event-section-btn");
        this.userSectionBtn = document.getElementById("user-section-btn");
        this.askSectionBtn = document.getElementById("ask-section-btn");

        this.dashboardSectionBtn.addEventListener("click", () => this.showDashboardSection());
        this.eventSectionBtn.addEventListener("click", () => this.showEventSection());
        this.userSectionBtn.addEventListener("click", () => this.showUserSection());
        this.askSectionBtn.addEventListener("click", () => this.showAskSection());

        this.isDashboardLoaded = false;
        this.isEventsLoaded = false;
        this.isUsersLoaded = false;
        this.isAsksLoaded = false;
    }

    /**
     *
     * @param name
     */
    showSection(name) {
        if(!name) return;
        Object.values(this.sections).forEach(section => section.style.display = "none");
        this.sections[name].style.display = "block";
    }

    /**
     *
     */
    showDashboardSection() {
        this.showSection("dashboard");

        if(this.isDashboardLoaded) return;
        loadDashboard().then(() => {
            this.isDashboardLoaded = true;
        });
    }

    /**
     *
     */
    showEventSection() {
        this.showSection("event");

        if(this.isEventsLoaded) return;

        if(!eventsPagination) eventsPagination = new Pagination("events-pagination", loadEvents);
        loadEvents().then(() => {
            this.isEventsLoaded = true;
        });
    }

    /**
     *
     */
    showUserSection() {
        this.showSection("user");

        if(this.isUsersLoaded) return;

        if(!usersPagination) usersPagination = new Pagination("users-pagination", loadUsers);

        loadUsers().then(() => {
            this.isUsersLoaded = true;
        });
    }

    /**
     *
     */
    showAskSection() {
        this.showSection("ask");

        if(this.isAsksLoaded) return;

        if(!asksPagination) asksPagination = new Pagination("asks-pagination", loadAsks);

        loadAsks().then(() => {
            this.isAsksLoaded = true;
        });
    }
}


/**
 * Charger les statistiques du tableau de bord
 * @returns {Promise<void>}
 */
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


/**
 * Charger les événements avec pagination
 * @param page
 * @returns {Promise<void>}
 */
const loadEvents = async (page = 1, request = null) => {
    if(!request ) {
        const searchQuery = document.getElementById("search-event-input").value.trim();
        const selectedStatus = document.getElementById("filter-event-status").value;
        request = `${FetchUtils.ADMIN_API_URL}/events?page=${page}`;
        if(searchQuery) request += `&search=${searchQuery}`;
        if(selectedStatus) request += `&status=${selectedStatus}`;
    }

    const response = await FetchUtils.fetch(request);
    const {events, totalPages} = response;
    lastEventRequest = request;

    const eventsTable = document.getElementById("events-table");
    eventsTable.innerHTML = "";

    if (!events || events.length === 0) {
        eventsTable.innerHTML = `<tr><td colspan="5" class="text-center text-danger">Aucun événement trouvé</td></tr>`;
        return;
    }

    events.forEach(event => {
        const row = document.createElement("tr");
        const eventStatusKey = event.status.key;
        row.innerHTML = `
            <td>${event.title}</td>
            <td>${event.organizer.pseudo}</td>
            <td>${new Date(event.startDateTime).toLocaleString()}</td>
            <td>
                <select class="form-select event-status" data-event-id="${event.uuid}">
                    <option value=${EventStatus.PENDING.key}" ${eventStatusKey === EventStatus.PENDING.key ? "selected" : ""}>${EventStatus.PENDING.label}</option>
                    <option value=${EventStatus.VALIDATED.key}" ${eventStatusKey === EventStatus.VALIDATED.key ? "selected" : ""}>${EventStatus.VALIDATED.label}</option>
                    <option value=${EventStatus.ON_GOING.key}" ${eventStatusKey === EventStatus.ON_GOING.key ? "selected" : ""}>${EventStatus.ON_GOING.label}</option>
                    <option value=${EventStatus.FULL.key}" ${eventStatusKey === EventStatus.FULL.key ? "selected" : ""}>${EventStatus.FULL.label}</option>
                    <option value=${EventStatus.CANCELLED.key}" ${eventStatusKey === EventStatus.CANCELLED.key ? "selected" : ""}>${EventStatus.CANCELLED.label}</option>
                    <option value=${EventStatus.CLOSED.key}" ${eventStatusKey === EventStatus.CLOSED.key ? "selected" : ""}>${EventStatus.CLOSED.label}</option>
                </select>
            </td>
        `;
        eventsTable.appendChild(row);
        eventsPagination.updatePagination(totalPages);
    });

    document.querySelectorAll(".event-status").forEach(select => {
        select.addEventListener("change", async (event) => {
            const eventId = event.target.getAttribute("data-event-id");
            const statusKey = event.target.value;
            showEventStatusChangeConfirmation(eventId, parseInt(statusKey));
        });
    });
}

/**
 * Mettre à jour le statut d’un événement (Valider/Suspendre)
 * @param eventId
 * @param statusKey
 * @returns {Promise<void>}
 */
async function updateEventStatus(eventId, statusKey) {
    const data = {eventUuid: selectedEventUuid, statusKey: selectedEventStatus.key};
    const response = await FetchUtils.fetch(`${FetchUtils.ADMIN_API_URL}/event-status`, "PUT", data);
    if (response.ok) {
        MessageUtils.success("Status mis à jour avec succès.");
        loadEvents(eventsPagination.currentPage, lastEventRequest).then();
    } else {
        const messages = Array.from(response.messages);
        messages.forEach(message => {
            MessageUtils.danger(message);
        });
    }
}

//
/**
 * Charger les utilisateurs et gérer les status
 * @param page
 * @returns {Promise<void>}
 */
async function loadUsers(page = 1, request = null) {
    if(!request) {
        const searchQuery = document.getElementById("searchUserInput").value.trim();
        const selectedStatus = document.getElementById("filterStatus").value;
        request = `${FetchUtils.ADMIN_API_URL}/users?page=${page}`;
        if(searchQuery) request += `&search=${searchQuery}`;
        if(selectedStatus) request += `&status=${selectedStatus}`;
    }

    const response = await FetchUtils.fetch(request);
    const {users, totalPages } = response;
    lastUserRequest = request;

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
            showUserStatusChangeConfirmation(userId, parseInt(statusKey));
        });
    });
}


/**
 * Mettre à jour le status d'un utilisateur
 * @param userId
 * @param statusKey
 * @returns {Promise<void>}
 */
async function updateUserStatus() {

    const data = {userUuid: selectedUserUuid, statusKey: selectedUserStatus.key};
    const response = await FetchUtils.fetch(`${FetchUtils.ADMIN_API_URL}/user-status`, "PUT", data);

    if (response.ok) {
        MessageUtils.success("Status mis à jour avec succès.");
        loadUsers(usersPagination.currentPage, lastEventRequest).then();
    } else {
        const messages = Array.from(response.messages);
        messages.forEach(message => {
            MessageUtils.danger(message);
        });
    }
}


//
/**
 * Charger les demandes et gérer les status
 * @param page
 * @returns {Promise<void>}
 */
async function loadAsks(page = 1, request = null) {
    if(!request) {
        const selectedAskType = document.getElementById("filter-ask-type").value.trim();
        const selectedAskStatus = document.getElementById("filter-ask-status").value;
        request = `${FetchUtils.ADMIN_API_URL}/asks?page=${page}`;
        if(selectedAskType) request += `&type=${selectedAskType}`;
        if(selectedAskStatus) request += `&status=${selectedAskStatus}`;
    }

    const response = await FetchUtils.fetch(request);
    const {asks, totalPages } = response;
    lastAskRequest = request;

    const asksTable = document.getElementById("asks-table");
    asksTable.innerHTML = "";

    if (!asks || asks.length === 0) {
        asksTable.innerHTML = `<tr><td colspan="4" class="text-center text-danger">Aucune demande trouvé</td></tr>`;
        return;
    }

    asks.forEach(ask => {
        const askStatusKey = ask.status.key;
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${ask.author.pseudo}</td>
            <td>${ask.type.label}</td>
            <td>${ask.message}</td>
            <td>
                <select class="form-select ask-status" data-ask-id="${ask.uuid}">
                    <option value="${AskStatus.PENDING.key}" ${askStatusKey === AskStatus.PENDING.key ? "selected" : ""}>${AskStatus.PENDING.label}</option>
                    <option value=${AskStatus.APPROVED.key}" ${askStatusKey === AskStatus.APPROVED.key ? "selected" : ""}>${AskStatus.APPROVED.label}</option>
                    <option value=${AskStatus.REJECTED.key}" ${askStatusKey === AskStatus.REJECTED.key ? "selected" : ""}>${AskStatus.REJECTED.label}</option>
                    
                </select>
            </td>
        `;
        asksTable.appendChild(row);
        asksPagination.updatePagination(totalPages)
    });


    document.querySelectorAll(".ask-status").forEach(select => {
        select.addEventListener("change", async (event) => {
            const askId = event.target.getAttribute("data-ask-id");
            const statusKey = event.target.value;
            showAskStatusChangeConfirmation(askId, parseInt(statusKey));
        });
    });
}

/**
 * Mettre à jour le status d'un utilisateur
 * @param userId
 * @param statusKey
 * @returns {Promise<void>}
 */
async function updateAskStatus() {
    const data = {uuid: selectedAskUuid, statusKey: selectedAskStatus.key};
    const response = await FetchUtils.fetch(`${FetchUtils.ADMIN_API_URL}/ask-status`, "PUT", data);
    if (response.ok) {
        MessageUtils.success("Status mis à jour avec succès.");
        loadAsks(asksPagination.currentPage, lastAskRequest).then();
    } else {
        const messages = Array.from(response.messages);
        messages.forEach(message => {
            MessageUtils.danger(message);
        });
    }
}


/**
 * Charger toutes les données au chargement de la page
 */
document.addEventListener("DOMContentLoaded", () => {
    const menu = new AdminMenu();
    //
    confirmUserModal = new window.bootstrap.Modal(document.getElementById("confirmUserStatusChangeModal"));
    const confirmUserStatusChangeBtn = document.getElementById("confirm-user-status-change-btn");
    if(confirmUserStatusChangeBtn) {
        confirmUserStatusChangeBtn.addEventListener("click", () => {
            updateUserStatus().then(() => {
                selectedUserUuid = null;
                selectedUserStatus = null;
                if(confirmUserModal) confirmUserModal.hide();
            })
        });
    }
    const searchUserBtn = document.getElementById("search-user-btn");
    if(searchUserBtn) {
        searchUserBtn.addEventListener("click", () => loadUsers());
    }

    //
    confirmEventModal = new window.bootstrap.Modal(document.getElementById("confirmEventStatusChangeModal"));
    const confirmEventStatusChangeBtn = document.getElementById("confirm-event-status-change-btn");
    if(confirmEventStatusChangeBtn) {
        confirmEventStatusChangeBtn.addEventListener("click", () => {
            updateEventStatus().then(() => {
                selectedEventUuid = null;
                selectedEventStatus = null;
                if(confirmEventModal) confirmEventModal.hide();
            })
        });
    }
    const searchEventBtn = document.getElementById("search-event-btn");
    if(searchEventBtn) {
        searchEventBtn.addEventListener("click", () => loadEvents());
    }

    //

    confirmAskModal = new window.bootstrap.Modal(document.getElementById("confirmAskStatusChangeModal"));
    const confirmAskStatusChangeBtn = document.getElementById("confirm-ask-status-change-btn");
    if(confirmAskStatusChangeBtn) {
        confirmAskStatusChangeBtn.addEventListener("click", () => {
            updateAskStatus().then(() => {
                selectedAskUuid = null;
                selectedAskStatus = null;
                if(confirmAskModal) confirmAskModal.hide();
            });
        });
    }
    const searchAskBtn = document.getElementById("search-ask-btn");
    if(searchAskBtn) {
        searchAskBtn.addEventListener("click", () => loadAsks());
    }
});
