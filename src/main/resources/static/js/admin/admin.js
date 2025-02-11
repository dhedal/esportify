import { FetchUtils } from "../utils/fetch-utils.js";

// Charger les utilisateurs
const loadUsers = async () => {

    const users = await FetchUtils.fetch(`${FetchUtils.ADMIN_API_URL}/users`);

    const usersTable = document.getElementById("usersTable");
    usersTable.innerHTML = "";

    if (!users || users.error) {
        usersTable.innerHTML = `<tr><td colspan="4" class="text-center text-danger">Aucun utilisateur trouvé</td></tr>`;
        return;
    }

    users.forEach(user => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${user.pseudo}</td>
            <td>${user.email}</td>
            <td>${user.status}</td>
            <td>${new Date(user.createdAt).toLocaleDateString()}</td>
        `;
        usersTable.appendChild(row);
    });
}

// Filtrer les utilisateurs
const filterUsers = () => {
    const searchQuery = document.getElementById("searchUser").value.toLowerCase();
    const selectedRole = document.getElementById("filterRole").value;

    document.querySelectorAll("#usersTable tr").forEach(row => {
        const name = row.children[0].innerText.toLowerCase();
        const role = row.children[2].innerText;

        const matchesSearch = name.includes(searchQuery);
        const matchesRole = selectedRole === "" || role === selectedRole;

        row.style.display = matchesSearch && matchesRole ? "" : "none";
    });
}

// Événements sur les filtres
document.getElementById("searchUser").addEventListener("input", filterUsers);
document.getElementById("filterRole").addEventListener("change", filterUsers);

// Charger les utilisateurs au chargement de la page
document.addEventListener("DOMContentLoaded", loadUsers);
