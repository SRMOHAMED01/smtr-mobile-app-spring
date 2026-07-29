# 1. Image de build : Compile le projet Java avec Maven
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Copie des fichiers de configuration Maven
COPY pom.xml .

# Téléchargement des dépendances
RUN mvn dependency:go-offline -B

# Copie du code source et compilation du JAR
COPY src ./src
RUN mvn package -DskipTests -B

# 2. Image d'exécution : Légère et optimisée
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copie du fichier .jar généré depuis l'étape de build
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
