import {FetchUtils} from "./utils/fetch-utils.js";

document.addEventListener("DOMContentLoaded", function() {
    console.log("Esportify - Page chargée");

    // Exemple : Activer le carrousel Bootstrap
    let carousel = new bootstrap.Carousel(document.querySelector("#carouselExample"), {
        interval: 3000
    });

    const logoutBtn = document.getElementById("logout");
    if(logoutBtn) {
        logoutBtn.addEventListener("click", (event) => {
            FetchUtils.logout();
        });
    }
});