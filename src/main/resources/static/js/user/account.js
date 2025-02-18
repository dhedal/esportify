import { FetchUtils } from "../utils/fetch-utils.js";
import { MessageUtils } from "../utils/message-utils.js";
import {FormValidator, Form} from "../utils/form-utils.js";



class PasswordForm extends Form {
    passwordHold;
    passwordNew
    passwordNewConfirmation;

    charLengthError;
    charLowerUpperError;
    charNumberError;
    charSpecialError;

    constructor(formElement = "password-form") {
        super(formElement, "password-submit");

        this.passwordHold = this._addInputs("password-hold", "keyup", FormValidator.validatePassword);
        this.passwordNew = this._addInputs("password-new", "keyup", this.validatePassword.bind(this));
        this.passwordNewConfirmation = this._addInputs("password-new-confirmation", "keyup",
            this.validatePasswordConfirmation.bind(this));

        this.charLengthError = document.getElementById("password-error-char-length");
        this.charLowerUpperError = document.getElementById("password-error-char-lower-upper");
        this.charNumberError = document.getElementById("password-error-char-number");
        this.charSpecialError = document.getElementById("password-error-char-special");
    }

    validatePassword(inputKey, input) {
        const value = input.value;

        const checkLength = FormValidator.validateStringLength(value);
        this.colorMessage(this.charLengthError, checkLength);

        const checkLowerUpper = FormValidator.validateStringContainsUpperAndLowerCase(value);
        this.colorMessage(this.charLowerUpperError, checkLowerUpper);

        const checkNumber = FormValidator.validateStringContainsCharNumber(value);
        this.colorMessage(this.charNumberError, checkNumber);

        const checkSpecial = FormValidator.validateStringContainsSpecialCharacters(value);
        this.colorMessage(this.charSpecialError, checkSpecial);

        if(checkLength && checkLowerUpper && checkNumber && checkSpecial) {
            input.classList.add("is-valid");
            input.classList.remove("is-invalid");
            return true;
        }
        input.classList.add("is-invalid");
        input.classList.remove("is-valid");
        return false;


    }
    validatePasswordConfirmation(inputKey, input) {
        const [passwordKey, passwordInput] = this._getInput(this.passwordNew);

        if(passwordInput.classList.contains("is-invalid") ||
            passwordInput.value.length === 0 ||
            !FormValidator.validateStringEquals(passwordInput.value, input.value))
        {
            input.classList.add("is-invalid");
            input.classList.remove("is-valid");
            return false;
        }

        input.classList.add("is-valid");
        input.classList.remove("is-invalid");
        return true;
    }

    colorMessage(message, ok) {
        if(ok) {
            message.classList.add("text-success");
            message.classList.remove("text-danger")
        }
        else {
            message.classList.remove("text-success");
            message.classList.add("text-danger");
        }
    }

    clearColorMessage( message) {
        message.classList.remove("text-success", "text-danger");
    }



    clear() {
        this._clear();
        this.clearColorMessage(this.charLengthError);
        this.clearColorMessage(this.charLowerUpperError);
        this.clearColorMessage(this.charNumberError);
        this.clearColorMessage(this.charSpecialError);
    }

    async send(data) {
        if(!data) return;

        const passordFormData = {
            passwordHold: data.get(this.passwordHold),
            passwordNew: data.get(this.passwordNew)
        };

        const response = await FetchUtils.fetch(`${FetchUtils.AUTH_API_URL}/password`, "PUT", passordFormData);
        if(response.ok) {
            MessageUtils.success("Le mot de passe a été changé, Veuillez vous reconnecter !");
        }
        else {
            const messages = Array.from(response.messages);
            messages.forEach(message => {
                MessageUtils.danger(message);
            });
        }
    }
}


// Fonction pour afficher une section et masquer les autres
const showSection = (sectionId) => {
    document.getElementById("profile-section").style.display = "none";
    document.getElementById("password-section").style.display = "none";
    document.getElementById("events-section").style.display = "none";
    document.getElementById("organizer-section").style.display = "none";
    document.getElementById(sectionId).style.display = "block";
}

// Charger les données utilisateur au chargement de la page
const loadUserProfile = async () => {
    const user = await FetchUtils.getCurrentUser();
    if (!user || user.error) {
        MessageUtils.danger("Erreur de chargement du profil");
        return;
    }

    document.getElementById("profile-pseudo").value = user.pseudo;
    document.getElementById("profile-email").value = user.email;
}


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
            <td>
                <a href="/events/detail/${event.uuid}" class="btn btn-link">Consulter</a>
            </td>
        `;
        eventsTable.appendChild(row);
    });
}

// demande d'obtention du status organizer
const requestOrganizerStatus = async () => {
    const response = await FetchUtils.fetch(FetchUtils.USER_API_URL + "/request-organizer");

    const messages = Array.from(response.messages);
    if (response.ok) {
        if(messages.length > 0) {
            messages.forEach(message => {
                MessageUtils.success(message);
            });
        }
        else {
            MessageUtils.success("Votre demande a été envoyée");
        }

    } else {
        messages.forEach(message => {
            MessageUtils.danger(message);
        });
    }
}


// Charger les données utilisateur et les événements
document.addEventListener("DOMContentLoaded", () => {
    const profileSectionBtn = document.getElementById("profile-section-btn");
    if(profileSectionBtn) profileSectionBtn.addEventListener("click",
        () => showSection("profile-section"));

    const passwordSectionBtn = document.getElementById("password-section-btn");
    if(passwordSectionBtn) passwordSectionBtn.addEventListener("click",
        () => showSection("password-section"));

    const eventsSectionBtn = document.getElementById("events-section-btn");
    if(eventsSectionBtn) eventsSectionBtn.addEventListener("click",
        () => showSection("events-section"));

    const organizerSectionBtn = document.getElementById("organizer-section-btn");
    if(organizerSectionBtn) organizerSectionBtn.addEventListener("click",
        () => showSection("organizer-section"));

    const requestOrganizerBtn = document.getElementById("request-organizer-btn");
    if(requestOrganizerBtn) requestOrganizerBtn.addEventListener("click",
        () => requestOrganizerStatus());


    const passwordForm = new PasswordForm();

    loadUserProfile().then();
    loadUserEvents().then();
});
