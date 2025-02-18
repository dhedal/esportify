import { FetchUtils } from "../utils/fetch-utils.js";
import { MessageUtils } from "../utils/message-utils.js";



/**
 *
 */
document.addEventListener("DOMContentLoaded", () => {
    const eventUuid = document.getElementById("eventUuid");

    const joinEventBtn = document.getElementById("join-event-btn");
    if(joinEventBtn) {
        joinEventBtn.addEventListener("click", async () => {
            const data = {uuid: eventUuid ? eventUuid.value : ""};
            const response = await FetchUtils.fetch(
                `${FetchUtils.EVENT_PARTICIPANT_API_URL}/join`, "POST", data);

            if (response.ok) {
                MessageUtils.success("Inscription réussie !");
                setTimeout(() => location.reload(), 1000); // Recharge la page pour mettre à jour l'état des boutons
            } else {
                const messages = Array.from(response.messages);
                messages.forEach(message => {
                    MessageUtils.danger(message);
                });
            }
        });
    }

    const leaveEventBtn = document.getElementById("leave-event-btn");
    if(leaveEventBtn) {
        leaveEventBtn.addEventListener("click", async () => {
            const data = {uuid: eventUuid ? eventUuid.value : ""};
            const response = await FetchUtils.fetch(
                `${FetchUtils.EVENT_PARTICIPANT_API_URL}/leave`, "POST", data);
            
            if (response.ok) {
                MessageUtils.success("Désinscription réussie !");
                setTimeout(() => location.reload(), 1000); // Recharge la page pour mettre à jour l'état des boutons
            } else {
                const messages = Array.from(response.messages);
                messages.forEach(message => {
                    MessageUtils.danger(message);
                });
            }
        });
    }
});


