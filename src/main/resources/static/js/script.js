import {FetchUtils} from "./utils/fetch-utils.js";

document.addEventListener("DOMContentLoaded", function() {
    console.log("Esportify - Page chargée");

    // Exemple : Activer le carrousel Bootstrap
    const carouselExample = document.querySelector("#carouselExample");
    if(carouselExample) {
        let carousel = new bootstrap.Carousel(carouselExample, {
            interval: 3000
        });
    }



    const logoutBtn = document.getElementById("logout");
    if(logoutBtn) {
        logoutBtn.addEventListener("click", (event) => {
            FetchUtils.logout();
        });
    }
});