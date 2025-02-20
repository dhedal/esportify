# Utilisation de l'image officielle Maven pour construire l'application
FROM maven:3.8-openjdk-21 AS build

# Définition du répertoire de travail
WORKDIR /app

# Copier les fichiers du projet
COPY . .

# Compiler l'application sans exécuter les tests
RUN mvn clean package -DskipTests

# Utilisation de l'image OpenJDK pour exécuter l'application
FROM openjdk:21-jdk-slim

# Copier le fichier .jar depuis l'étape précédente
COPY --from=build /app/target/*.jar app.jar

# Lancer l'application
ENTRYPOINT ["java", "-jar", "/app.jar"]

# Exposer le port de l'application
EXPOSE 8080
