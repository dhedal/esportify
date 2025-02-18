export class FormValidator {
    static validateInputRequired = (input) => {
        return input.required && input.value !== "";
    }

    static validateStringEquals = (s1, s2) => {
        return s1 === s2;
    }

    static validateEmailInput = (email) => {
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value);
    }

    static validateStringLength(string = "", minLength = 8){
        return !(string.length < minLength);
    }

    static validateStringContainsUpperAndLowerCase(string = ""){
        return /[A-Z]/.test(string) && /[a-z]/.test(string);
    }

    static validateStringContainsCharNumber(string = ""){
        return /[0-9]/.test(string);
    }

    static validateStringContainsOnlyZipCode(zipcode = "") {
        return /^[0-9]{5}(?:-[0-9]{4})?$/.test(zipcode);
    }

    static validateStringContainsSpecialCharacters(string = "", pattern = /[!@#$%&*?:+-_]/){
        return pattern.test(string);
    }

    static validateInputNotEmpty(inputKey, input) {
        if(FormValidator.validateInputRequired(input)) {
            input.classList.add("is-valid");
            input.classList.remove("is-invalid");
            return true;
        }
        input.classList.add("is-invalid");
        input.classList.remove("is-valid");
        return false;
    }

    static validateEmail(inputKey, input) {
        if(FormValidator.validateEmailInput(input)) {
            input.classList.add("is-valid");
            input.classList.remove("is-invalid");
            return true;
        }

        input.classList.add("is-invalid");
        input.classList.remove("is-valid");
        return false;
    }

    static validatePassword(inputKey, input) {
        const value = input.value;
        const isValid = !Array.of(
            FormValidator.validateStringLength(value),
            FormValidator.validateStringContainsUpperAndLowerCase(value),
            FormValidator.validateStringContainsCharNumber(value),
            FormValidator.validateStringContainsSpecialCharacters(value)
        ).includes(false);

        if(isValid) {
            input.classList.add("is-valid");
            input.classList.remove("is-invalid");
            return true;
        }
        input.classList.add("is-invalid");
        input.classList.remove("is-valid");
        return false;
    }
}

export class Form {
    _formElement;
    _fnMap;
    _inputArray;
    _submit;
    constructor(formElement, submitElement) {
        this._formElement = document.getElementById(formElement);
        this._submit = document.getElementById(submitElement);
        this._submit.disabled = true;
        this._fnMap = new Map();
        this._inputArray = new Array();

        this._submit.addEventListener("click", event => {
            event.preventDefault();
            event.stopPropagation();
            this.send(this._extractData());
        });
    }

    _getInput(inputKey) {
        return this._inputArray.find(([key, input]) => inputKey === key);
    }

    _setInputValue(inputId, value) {
        const inputArr = this._inputArray.find(input => input[0] === inputId);
        if (inputArr) inputArr[1].value = value;
    }


    _addInputs(inputId, eventType, fnValidate) {
        const input = document.getElementById(inputId);
        this._fnMap.set(inputId, fnValidate);
        this._inputArray.push([inputId, input]);
        input.addEventListener(eventType, event => {
            event.stopPropagation();
            this.validateForm();
        });
        return inputId
    }

    validateForm() {
        this._validateForm();
    }

    _validateForm() {
        this._submit.disabled = this._inputArray
            .map(([key, input]) => this._fnMap.get(key)(key, input))
            .includes(false);
    }

    _extractData() {
        const data = new Map();
        this._inputArray.forEach(([inputName, input]) => {
            data.set(inputName, input.value);
        });
        return data;
    }

    async send(data) {
    }

    _clearValidOrInvalidCSS = (element) => {
        if(element.classList.contains("is-valid")) {
            element.classList.remove("is-valid");
        }
        else {
            element.classList.remove("is-invalid");
        }
    }

    _clear() {
        this._inputArray.forEach(([elementKey, element]) => {
            this._clearValidOrInvalidCSS(element);
            if(element.tagName === "INPUT" || element.tagName === "TEXTAREA") element.value = "";
            else element.textContent = "";
        });
    }

    clear(){
        this._clear();
    }

}