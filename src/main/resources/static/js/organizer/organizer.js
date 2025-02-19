import { FetchUtils } from "../utils/fetch-utils.js";
import { MessageUtils } from "../utils/message-utils.js";
import {FormValidator, Form} from "../utils/form-utils.js";
import {Pagination} from "../utils/pagination-utils.js";
import {EventStatus} from "../utils/data-utils.js"

let eventsPagination;
let eventForm;
let eventModal;
let participantsModal;

const TIME_MODIFY_LIMIT = 30 * 60 * 1000;
/**
 *
 */
class OrganizerMenu {
    dashboardSectionBtn;
    eventSectionBtn;

    sections;

    isDashboardLoaded;
    isEventsLoaded;

    constructor(containerId = "organizer-sidebar") {
        this.sections = {
            dashboard: document.getElementById("dashboard-section"),
            event: document.getElementById("event-section")
        };

        this.dashboardSectionBtn = document.getElementById("dashboard-section-btn");
        this.eventSectionBtn = document.getElementById("event-section-btn");

        this.dashboardSectionBtn.addEventListener("click", () => this.showDashboardSection());
        this.eventSectionBtn.addEventListener("click", () => this.showEventSection());

        this.isDashboardLoaded = false;
        this.isEventsLoaded = false;
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
        this.isDashboardLoaded = true;
    }

    /**
     *
     */
    showEventSection() {
        this.showSection("event");

        if(this.isEventsLoaded) return;

        if(!eventsPagination) eventsPagination = new Pagination("events-pagination", loadOrganizerEvents);
        loadOrganizerEvents().then(() => {
            this.isEventsLoaded = true;
        });
    }

}

/**
 *
 */
class EventForm extends Form {
    title;
    description;
    maxPlayers;
    startDateTime;
    endDateTime;

    constructor(formElement = "event-form") {
        super(formElement, "event-submit");
        this.title = this._addInputs("title-event-form", "keyup", FormValidator.validateInputNotEmpty);
        this.description = this._addInputs("description-event-form", "keyup", FormValidator.validateInputNotEmpty);
        this.maxPlayers = this._addInputs("maxPlayers-event-form", "keyup", FormValidator.validateInputNotEmpty);
        this.startDateTime = this._addInputs("startDateTime-event-form", "keyup", FormValidator.validateInputNotEmpty);
        this.endDateTime = this._addInputs("endDateTime-event-form", "keyup", FormValidator.validateInputNotEmpty);
    }

    async send(data) {
        const now = new Date();
        const startDate = new Date(data.get(this.startDateTime));
        const endDate = new Date(data.get(this.endDateTime));
        const minCreateDate = new Date(now.getTime() + 3 * 24 * 60 * 60 * 1000);

        if (startDate < minCreateDate) {
            MessageUtils.danger("L'événement doit être créé au moins 3 jours avant son début.");
            return;
        }

        const eventData = {
            title: data.get(this.title),
            description: data.get(this.description),
            maxPlayers: data.get(this.maxPlayers),
            startDateTime: data.get(this.startDateTime),
            endDateTime: data.get(this.endDateTime),
        };

        let method = "POST";
        if(this.event) {
            eventData.uuid = this.event.uuid;
            method = "PUT";
        }

        const response = await FetchUtils.fetch(`${FetchUtils.EVENT_API_URL}/event`, method, eventData);
        if (response.messages) {
            const messages = Array.from(response.messages);
            const messageType = response.ok ? MessageUtils.MESSAGE_TYPE_SUCCESS : MessageUtils.MESSAGE_TYPE_DANGER;
            messages.forEach(message => {
                MessageUtils.message(messageType, message);
            });
            eventModal.hide();
        }

        if(response.ok) {
            this.clear();
            loadOrganizerEvents().then();
        }
    }

    setEvent(event) {
        if(!event) return;
        this._setInputValue(this.title, event.title);
        this._setInputValue(this.description, event.description);
        this._setInputValue(this.maxPlayers, event.maxPlayers);
        this._setInputValue(this.startDateTime, event.startDateTime);
        this._setInputValue(this.endDateTime, event.endDateTime);
        this.event = event;

    }

    clear() {
        super.clear();
        if(this.event) this.event = null;
    }
}

/**
 *
 * @returns {Promise<void>}
 */
const loadOrganizerEvents = async () => {
    const events = await FetchUtils.fetch(FetchUtils.EVENT_API_URL + "/my-events");
    const eventsTable = document.getElementById("eventsTable");
    eventsTable.innerHTML = "";

    if (!events || events.error) {
        eventsTable.innerHTML = `<tr><td colspan="5" class="text-center text-danger">Aucun événement trouvé</td></tr>`;
        return;
    }

    events.forEach(event => {
        const row = document.createElement("tr");
        const startDate = new Date(event.startDateTime);
        const now = new Date();
        const canModify = startDate - now > TIME_MODIFY_LIMIT;
        const canStart = (startDate - now <= TIME_MODIFY_LIMIT) &&
            (event.status.key === EventStatus.VALIDATED.key || event.status.key === EventStatus.FULL.key);

        row.innerHTML = `
            <td>${event.title}</td>
            <td>${new Date(event.startDateTime).toLocaleString()}</td>
            <td>${event.status.label}</td>
            <td>${event.maxPlayers}</td>
            <td>
                <div class="btn-group">
                    <button class="btn btn-outline-info btn-sm btn-participants" data-event-id="${event.uuid}">Voir les participants</button>
                    ${canModify ? `<button class="btn btn-warning btn-sm btn-edit" data-event-id="${event.uuid}">Modifier</button>` : ""}
                    ${canStart ? `<button class="btn btn-success btn-sm btn-start" data-event-id="${event.uuid}">Démarrer</button>` : ""}
                </div>
            </td>
        `;

        eventsTable.appendChild(row);

        // Ajouter les événements "Modifier"
        document.querySelectorAll(".btn-edit").forEach(button => {
            button.addEventListener("click", (event) => {
                const eventId = event.target.getAttribute("data-event-id");
                openEventModal (eventId);
            });
        });

    });
};

/**
 *
 * @param eventId
 * @returns {Promise<void>}
 */
const openEventModal = async (eventId = null) => {
    const modalTitle = document.getElementById("eventModalLabel");
    const submitButton = document.getElementById("event-submit");

    if (eventId) {
        // Mode modification
        modalTitle.innerText = "Modifier l'événement";
        submitButton.innerText = "Enregistrer les modifications";

        const response = await FetchUtils.fetch(`${FetchUtils.EVENT_API_URL}/${eventId}`);

        if (!response || response.error) {
            MessageUtils.danger("Impossible de charger l'événement.");
            return;
        }

        eventForm.setEvent(response);
    } else {
        // Mode création
        modalTitle.innerText = "Créer un événement";
        submitButton.innerText = "Créer";
        eventForm.clear();
    }

    eventModal.show();
};


/**
 *
 * @param eventId
 * @returns {Promise<void>}
 */
const openParticipantsModal = async (eventId) => {
    const response = await FetchUtils.fetch(`${FetchUtils.EVENT_PARTICIPANT_API_URL}/participants`, "POST", { uuid: eventId });

    const participantsTable = document.getElementById("participantsTable");
    participantsTable.innerHTML = "";

    if (!response || !response.ok) {
        participantsTable.innerHTML = `<tr><td colspan="3" class="text-center text-danger">Aucun participant</td></tr>`;
        return;
    }

    document.getElementById("participantsModalLabel").innerText = `Participants - ${response.event.title}`;

    const participants = response.participants;
    participants.forEach(participant => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${participant.participant.pseudo}</td>
            <td>${participant.status.label}</td>
            <td>
                <button class="btn btn-danger btn-sm btn-reject" data-event-id="${eventId}" data-participant-id="${participant.participant.uuid}">Rejeter</button>
            </td>
        `;
        participantsTable.appendChild(row);
    });

    // Ajouter les événements "Rejeter"
    document.querySelectorAll(".btn-reject").forEach(button => {
        button.addEventListener("click", (event) => {
            const eventId = event.target.getAttribute("data-event-id");
            const participantId = event.target.getAttribute("data-participant-id");
            rejectParticipant(eventId, participantId);
        });
    });

    participantsModal.show();
};

/**
 * Rejeter un participant
 * @param eventId
 * @param participantId
 */
const rejectParticipant = async (eventId, participantId) => {

    if(!confirm("Voulez-vous vraiment rejeter ce participant ? Cette action est irréversible.")) {
        return;
    }
    const data = {
        eventUuid: eventId,
        participantUuid: participantId
    };
    const response = await FetchUtils.fetch(`${FetchUtils.EVENT_PARTICIPANT_API_URL}/reject`, "POST", data);

    if (response.ok) {
        MessageUtils.success("Participant rejeté.");
    } else {
        const messages = Array.from(response.messages);
        messages.forEach(message => {
            MessageUtils.danger(message);
        });
    }
};



/**
 *
 */
const ready = () => {
    const organizerMenu = new OrganizerMenu();
    eventForm = new EventForm();
    eventModal = new window.bootstrap.Modal(document.getElementById("eventModal"));
    participantsModal = new window.bootstrap.Modal(document.getElementById("participantsModal"));

    const createEventBtn = document.getElementById("create-event-btn");
    if(createEventBtn) {
        createEventBtn.addEventListener("click", () => {
            openEventModal().then();
        });
    }

    document.addEventListener("click", (event) => {
        if (event.target.classList.contains("btn-participants")) {
            const eventId = event.target.getAttribute("data-event-id");
            openParticipantsModal(eventId).then();
        }
    });

    loadOrganizerEvents().then();
};

document.addEventListener("DOMContentLoaded", ready);
