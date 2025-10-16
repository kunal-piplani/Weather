# ---------- Stage 1: Build ----------
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the entire project (no caching step)
COPY . .

# Build directly (fetches fresh dependencies)
RUN mvn clean package -DskipTests

# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8081
EXPOSE 8081

# Pass API key at runtime (not stored in image)
ENV OWM_API_KEY="d32b8c8e2deb11f0c15f6b8347799b13"

# Optional: mount volume for SQLite data persistence
VOLUME ["/data"]

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
