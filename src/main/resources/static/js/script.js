import {FetchUtils} from "./utils/fetch-utils.js";
import {MessageUtils} from "./utils/message-utils.js";

document.addEventListener("DOMContentLoaded", function() {
    console.log("Esportify - Page chargée");

    // Exemple : Activer le carrousel Bootstrap
    const carouselExample = document.querySelector("#carouselExample");
    if(carouselExample) {
        let carousel = new window.bootstrap.Carousel(carouselExample, {
            interval: 3000
        });
    }

    const logoutMessage = document.getElementById("logoutMessage");
    if(logoutMessage) {
        MessageUtils.success(logoutMessage.value);
    }
});