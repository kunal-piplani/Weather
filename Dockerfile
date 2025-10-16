# ---------- Stage 1: Build ----------
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Copy pom.xml and download dependencies first (for caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the rest of the project and build it
COPY src ./src
RUN mvn clean package -DskipTests

# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8081 (since your app runs on this port)
EXPOSE 8081
ENV OWM_API_KEY="d32b8c8e2deb11f0c15f6b8347799b13"
VOLUME ["/data"]

# Start the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]