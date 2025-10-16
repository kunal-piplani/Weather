FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and download dependencies (with retry in case of network issue)
COPY pom.xml .
RUN mvn dependency:go-offline -B || mvn dependency:go-offline -B

# Copy the source code and build the JAR
COPY src ./src
RUN mvn clean package -DskipTests

# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose the app port
EXPOSE 8081

# Pass the API key at runtime instead of hardcoding it
# Use: docker run -e OWM_API_KEY="your_api_key_here" ...
ENV OWM_API_KEY="d32b8c8e2deb11f0c15f6b8347799b13"

# Optional: volume for persistent data
VOLUME ["/data"]

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
