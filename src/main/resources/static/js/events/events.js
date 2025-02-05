import {FetchUtils} from "../utils/fetch-utils.js";
import {MessageUtils} from "../utils/message-utils.js";

const registerToEvent = async (eventId) => {
    const response = await FetchUtils.fetch(`${FetchUtils.EVENT_PARTICIPANT_API_URL}/join`, "POST",
        {uuid: eventId});
    if(response.ok) {
        MessageUtils.success("Votre demande d'inscription est en cour de validation !");
    }
    else {
        const messages = Array.from(response.messages);
        messages.forEach(message => {
            MessageUtils.danger(message);
        });
    }
}


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
            <td>
                <button class="btn btn-success register-btn" data-event-id="${event.uuid}">S'inscrire</button>
            </td>
        `;
        eventsTable.appendChild(row);
    });

    document.querySelectorAll(".register-btn").forEach(button => {
        button.addEventListener("click", async (event) => {
            const eventId = event.target.getAttribute("data-event-id");
            await registerToEvent(eventId);
        });
    });


};

document.addEventListener("DOMContentLoaded", loadEvents);