# Installation de l'environnement en local

## 🚀 Installation et Configuration de l'Environnement

### 📌 Prérequis
Avant de commencer, assurez-vous d'avoir installé les outils suivants :

- [Java 21+](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
- [Maven](https://maven.apache.org/download.cgi)

## 1️⃣ Installation de MySQL 8+

1. Téléchargez et installez [MySQL 8+](https://dev.mysql.com/downloads/).
2. Assurez-vous que MySQL est en cours d’exécution.
3. Connectez-vous à MySQL avec un utilisateur ayant les droits d’administration :
   ```bash
   mysql -u root -p
   ```
4. Exécutez les scripts SQL situés dans le dossier `SQL/` à la racine du projet :
   ```sql
   SOURCE SQL/schema.sql;
   SOURCE SQL/insert_data.sql;
   ```

### 🔹 Particularité des utilisateurs
- Tous les utilisateurs ont le mot de passe : **StrongPassword!1**
- Les mots de passe sont cryptés dans la base de données.
- Pour distinguer les rôles (**Admin, Organizer, Player**), consultez leurs emails.

## 2️⃣ Configuration des variables d’environnement
Ajoutez les variables d’environnement suivantes :

```bash
export ES_DB_USER="votre_utilisateur"
export ES_DB_PASSWORD="votre_mot_de_passe"
export ES_DB_URL="jdbc:mysql://localhost:3306/esportify"
export ES_SERVER_URL="http://localhost:8080"
```

📌 **Remarque** : Si `ES_SERVER_URL` n'est pas défini, la valeur par défaut sera `http://localhost:8080`.

Pour rendre ces variables permanentes, ajoutez-les à votre fichier `~/.bashrc` ou `~/.zshrc` et rechargez-le :

```bash
source ~/.bashrc  # ou source ~/.zshrc selon votre terminal
```

Vérifiez que les variables sont bien définies :

```bash
echo $ES_DB_USER
echo $ES_DB_PASSWORD
echo $ES_DB_URL
echo $ES_SERVER_URL
```

## 3️⃣ Exécution du projet Spring Boot (Maven)

Assurez-vous que Maven est installé, puis lancez l’application :

```bash
mvn spring-boot:run
```

L’application sera accessible sur `${ES_SERVER_URL}` (par défaut `http://localhost:8080`).

---

Votre environnement est maintenant prêt à être utilisé ! 🚀

