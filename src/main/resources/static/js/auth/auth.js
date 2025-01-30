import {fetchData} from "../utils/fetch-utils.js";

document.getElementById("loginForm").addEventListener("submit", async (event) => {
    event.preventDefault();

    const email = document.getElementById("email-login").value;
    const password = document.getElementById("password-login").value;

    const data = {email: email, password:password};
    console.log(data);

    const response = await fetchData("http://localhost:8080/api/auth/login", "POST", data);
    console.log(response)

    if (response.ok) {
        // window.location.href = "/"; // Redirige après connexion
    } else {
        document.getElementById("loginMessage").innerText = response.message;
    }
});