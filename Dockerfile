# Étape 1 : Construire l'application avec Maven
FROM maven:3.8.8-eclipse-temurin-21 AS build

# Définition du répertoire de travail
WORKDIR /app

# Copier les fichiers du projet
COPY . .

# Compiler l'application sans exécuter les tests
RUN mvn clean package -DskipTests

# Étape 2 : Exécuter l'application avec OpenJDK
FROM openjdk:21-jdk-slim

# Définition du répertoire de travail pour l'exécution
WORKDIR /app

# Copier le fichier .jar depuis l'étape précédente
COPY --from=build /app/target/*.jar app.jar

# Lancer l'application
ENTRYPOINT ["java", "-jar", "/app.jar"]

# Exposer le port de l'application
EXPOSE 8080
