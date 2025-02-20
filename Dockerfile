# Utilisation d'une image officielle OpenJDK pour exécuter l'application
FROM openjdk:21-jdk-slim

# Définir l'argument pour le fichier JAR
ARG JAR_FILE=target/*.jar

# Copier le fichier JAR dans l'image
COPY ${JAR_FILE} app.jar

# Exécuter l'application Spring Boot
ENTRYPOINT ["java", "-jar", "/app.jar"]

# Exposer le port 8080 pour l'application
EXPOSE 8080