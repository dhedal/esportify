import { FetchUtils } from "../utils/fetch-utils.js";
import { MessageUtils } from "../utils/message-utils.js";

// Fonction pour afficher une section et masquer les autres
const showSection = (sectionId) => {
    document.getElementById("profile-section").style.display = "none";
    document.getElementById("events-section").style.display = "none";
    document.getElementById("organizer-section").style.display = "none";
    document.getElementById(sectionId).style.display = "block";
}

// Charger les données utilisateur au chargement de la page
const loadUserProfile = async () => {
    const user = await FetchUtils.getCurrentUser();
    console.log(user);
    if (!user || user.error) {
        MessageUtils.danger("Erreur de chargement du profil");
        return;
    }

    document.getElementById("pseudo").value = user.pseudo;
    document.getElementById("email").value = user.email;
}

// Soumission du formulaire de mise à jour du profil
document.getElementById("profileForm").addEventListener("submit", async (event) => {
    event.preventDefault();

    const pseudo = document.getElementById("pseudo").value;
    const newPassword = document.getElementById("newPassword").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    if (newPassword && newPassword !== confirmPassword) {
        MessageUtils.danger("Les mots de passe ne correspondent pas");
        return;
    }

    const response = await FetchUtils.fetch(FetchUtils.AUTH_API_URL + "/api/user/profile", "PUT", { pseudo, newPassword });

    if (response.ok) {
        MessageUtils.success("Profil mis à jour avec succès");
        setTimeout(() => location.reload(), 1000);
    } else {
        const messages = Array.from(response.messages);
        messages.forEach(message => {
            MessageUtils.danger(message);
        });
    }
});

// Charger les événements de l'utilisateur
const loadUserEvents = async (user) => {
    const eventParticipants = await FetchUtils.fetch( FetchUtils.USER_API_URL + "/my-events");

    const eventsTable = document.getElementById("eventsTable");
    eventsTable.innerHTML = "";

    if (!eventParticipants || eventParticipants.error || eventParticipants.length === 0) {
        eventsTable.innerHTML = `<tr><td colspan="3" class="text-center text-danger">Aucun événement trouvé</td></tr>`;
        return;
    }

    eventParticipants.forEach(eventParticipant => {
        const event = eventParticipant.event;
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${event.title}</td>
            <td>${new Date(event.startDateTime).toLocaleDateString()}</td>
            <td>${eventParticipant.status.label}</td>
        `;
        eventsTable.appendChild(row);
    });
}

// Demande de passage en organisateur
document.getElementById("requestOrganizer").addEventListener("click", async () => {
    const response = await FetchUtils.fetch("/api/user/request-organizer", "POST");

    if (response.ok) {
        MessageUtils.success("Votre demande a été envoyée");
    } else {
        const messages = Array.from(response.messages);
        messages.forEach(message => {
            MessageUtils.danger(message);
        });
    }
});

// Charger les données utilisateur et les événements
document.addEventListener("DOMContentLoaded", () => {
    const profileSectionBtn = document.getElementById("profile-section-btn");
    if(profileSectionBtn) profileSectionBtn.addEventListener("click",
        () => showSection("profile-section"));

    const eventsSectionBtn = document.getElementById("events-section-btn");
    if(eventsSectionBtn) eventsSectionBtn.addEventListener("click",
        () => showSection("events-section"));

    const organizerSectionBtn = document.getElementById("organizer-section-btn");
    if(organizerSectionBtn) organizerSectionBtn.addEventListener("click",
        () => showSection("organizer-section"));


    loadUserProfile().then();
    loadUserEvents().then();
});
