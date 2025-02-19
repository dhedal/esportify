

export class FetchUtils {
    static USER_KEY = "user";
    static SERVER_URL = "http://localhost:8080"
    static API_URL = FetchUtils.SERVER_URL + "/api";
    static AUTH_API_URL = FetchUtils.API_URL + "/auth";
    static EVENT_API_URL = FetchUtils.API_URL + "/events";
    static EVENT_PARTICIPANT_API_URL = FetchUtils.API_URL + "/event-participant";
    static USER_API_URL = FetchUtils.API_URL + "/user";
    static ADMIN_API_URL = FetchUtils.API_URL + "/admin";
    static async fetch(url, method = "GET", body = null) {
        const headers = { "Content-Type": "application/json" };

        const options = {
            method,
            headers,
            credentials: "include",
        };

        if (body) {
            options.body = JSON.stringify(body);
        }

        try {
            const response = await fetch(url, options);
            return await response.json();
        } catch (error) {
            console.error("Erreur lors de la requête API :", error);
            return { error: "Une erreur est survenue." };
        }
    }


    static async getCurrentUser(){
        return await FetchUtils.fetch(FetchUtils.AUTH_API_URL + "/me");
    }
}
