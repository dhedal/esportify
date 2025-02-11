import { FetchUtils } from "../utils/fetch-utils.js";
import { MessageUtils } from "../utils/message-utils.js";
import {FormValidator, Form} from "../utils/form-utils.js";

class EventForm extends Form{

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
        const eventData = {
            title: data.get(this.title),
            description: data.get(this.description),
            maxPlayers: data.get(this.maxPlayers),
            startDateTime: data.get(this.startDateTime),
            endDateTime: data.get(this.endDateTime),
        };

        const response = await FetchUtils.fetch(`${FetchUtils.EVENT_API_URL}/event`, "POST", eventData);
        if(response && response.messages) {
            const messages = Array.from(response.messages);
            const messageType = response.ok ? MessageUtils.MESSAGE_TYPE_SUCCESS : MessageUtils.MESSAGE_TYPE_DANGER;
            messages.forEach(message => {
                MessageUtils.message(messageType, message);
            });
            new window.bootstrap.Modal(document.getElementById("createEventModal")).hide();
        }
        else {
            MessageUtils.danger("Une erreur interne est survenue, réessayer ultérieurement !")
        }
    }

}

// Charger les événements de l'organisateur
const loadOrganizerEvents = async () => {
    const events = await FetchUtils.fetch(FetchUtils.EVENT_API_URL + "/my-events");
    console.log(events);

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
            <td>${new Date(event.startDateTime).toLocaleString()}</td>
            <td>${event.status.label}</td>
            <td>${event.maxPlayers}</td>
            <td>
                <div class="btn-group">
                    <button class="btn btn-outline-info btn-sm btn-manage" data-event-id="${event.uuid}">Gérer</button>
                    ${canStartEvent(event.startDateTime) ? `<button class="btn btn-success btn-sm btn-start" data-event-id="${event.uuid}">Démarrer</button>` : ""}
                </div>
            </td>
        `;

        eventsTable.appendChild(row);
    });

    // Ajouter les événements "Gérer"
    document.querySelectorAll(".btn-manage").forEach(button => {
        button.addEventListener("click", (event) => {
            const eventId = event.target.getAttribute("data-event-id");
            openParticipantsModal(eventId);
        });
    });

    // Ajouter les événements "Démarrer"
    document.querySelectorAll(".btn-start").forEach(button => {
        button.addEventListener("click", (event) => {
            const eventId = event.target.getAttribute("data-event-id");
            startEvent(eventId);
        });
    });
}

// Vérifie si l'événement peut être démarré (30 min avant le début)
const canStartEvent = (startDateTime) => {
    const startTime = new Date(startDateTime);
    const now = new Date();
    return (startTime - now) <= 30 * 60 * 1000 && now < startTime;
}

// Démarrer un événement
const startEvent = async (eventId) => {
    const response = await FetchUtils.fetch(`${FetchUtils.EVENT_API_URL}/start-event`, "POST", {uuid: eventId});

    if (response.ok) {
        MessageUtils.success("Événement démarré !");
        loadOrganizerEvents();
    } else {
        MessageUtils.danger("Erreur lors du démarrage.");
    }
}

// Ouvrir le modal des participants
const openParticipantsModal = async (eventId) => {
    const response = await FetchUtils.fetch(`${FetchUtils.EVENT_PARTICIPANT_API_URL}/participants`, "POST", {uuid:eventId});
    console.log(response);

    const participantsTable = document.getElementById("participantsTable");
    participantsTable.innerHTML = "";

    if (!response || !response.ok) {
        participantsTable.innerHTML = `<tr><td colspan="3" class="text-center text-danger">Aucun participant</td></tr>`;
        return;
    }

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

    new window.bootstrap.Modal(document.getElementById("participantsModal")).show();
}

/// Rejeter un participant
const rejectParticipant = async (eventId, participantId) => {
    console.log("Rejet du participant ID :", participantId, " de l'événement ID :", eventId);
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

const ready = () => {
    loadOrganizerEvents().then();
    const eventForm = new EventForm();
};

// Charger les événements de l'organisateur au chargement de la page
document.addEventListener("DOMContentLoaded", ready);
