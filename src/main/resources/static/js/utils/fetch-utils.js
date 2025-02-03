export class FetchUtils {
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
}
