# Étape 1 : Construire l'application avec Maven
FROM maven:3.8.8-eclipse-temurin-21 AS build

WORKDIR /app
COPY . .

# Compiler l'application sans exécuter les tests
RUN mvn clean package -DskipTests

# Étape 2 : Exécuter l'application avec OpenJDK
FROM openjdk:21-jdk-slim

WORKDIR /app
COPY --from=build /app/target/app.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE 8080
