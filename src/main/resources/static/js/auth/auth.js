import {FetchUtils} from "../utils/fetch-utils.js";
import {FormValidator, Form} from "../utils/form-utils.js";
import {MessageUtils} from "../utils/message-utils.js";

// document.getElementById("loginForm").addEventListener("submit", async (event) => {
//     event.preventDefault();
//
//     const email = document.getElementById("email-login").value;
//     const password = document.getElementById("password-login").value;
//
//     const data = {email: email, password:password};
//     console.log(data);
//
//     const response = await fetchData("http://localhost:8080/api/auth/login", "POST", data);
//     console.log(response)
//
//     if (response.ok) {
//         // window.location.href = "/"; // Redirige après connexion
//     } else {
//         document.getElementById("loginMessage").innerText = response.message;
//     }
// });




class LoginForm extends Form {
    email;
    password;

    constructor(formElement = "login-form") {
        super(formElement, "login-submit");
        this.email = this._addInputs("login-email", "keyup", FormValidator.validateEmail);
        this.password = this._addInputs("login-password", "keyup", FormValidator.validatePassword);
    }

    async send(data) {
        if(!data) return;

        const loginData = {
            email: data.get(this.email),
            password: data.get(this.password)
        };
        console.log(loginData);

        const response = await FetchUtils.fetch(`${FetchUtils.API_AUTH_URL}/login`, "POST", loginData);
        console.log(response);
        if(response.ok && response.userDTO) {
            localStorage.setItem(FetchUtils.USER_KEY, JSON.stringify(response.userDTO));
            window.location.href = "/";
        }
        else {
            const messages = Array.from(response.messages);
            messages.forEach(message => {
                MessageUtils.danger(message);
            });
        }
    }
}

class RegisterForm extends Form {
    pseudo;
    email;
    password;
    confirmPassword;

    charLengthError;
    charLowerUpperError;
    charNumberError;
    charSpecialError;

    constructor(formElement = "register-form") {
        super(formElement, "register-submit");

        this.pseudo = this._addInputs("register-pseudo", "keyup", FormValidator.validateInputNotEmpty);
        this.email = this._addInputs("register-email", "keyup", FormValidator.validateEmail);
        this.password = this._addInputs("register-password", "keyup", this.validatePassword.bind(this));
        this.confirmPassword = this._addInputs("register-confirm-password", "keyup",
            this.validateConfirmPassword.bind(this));

        this.charLengthError = document.getElementById("register-error-char-length");
        this.charLowerUpperError = document.getElementById("register-error-char-lower-upper");
        this.charNumberError = document.getElementById("register-error-char-number");
        this.charSpecialError = document.getElementById("register-error-char-special");
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
    validateConfirmPassword(inputKey, input) {
        const [passwordKey, passwordInput] = this._getInput(this.password);

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

        const registerData = {
            pseudo: data.get(this.pseudo),
            email: data.get(this.email),
            password: data.get(this.password)
        };
        console.log(registerData);

        const response = await FetchUtils.fetch(`${FetchUtils.API_AUTH_URL}/register`, "POST", registerData);
        console.log(response);
        if(response.ok) {}
        else {
            const messages = Array.from(response.messages);
            messages.forEach(message => {
                MessageUtils.danger(message);
            });
        }
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const loginForm = new LoginForm();
    const registerForm = new RegisterForm();
});



