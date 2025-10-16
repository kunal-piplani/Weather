# ---------- Stage 1: Build ----------
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Add Maven Central mirror for stability (fixes network/DNS/VPN issues)
RUN mkdir -p /root/.m2 && echo '<settings><mirrors><mirror><id>central</id><mirrorOf>*</mirrorOf><url>https://repo1.maven.org/maven2/</url></mirror></mirrors></settings>' > /root/.m2/settings.xml

# Copy entire project
COPY . .

# Build the Spring Boot app (no tests to speed up)
RUN mvn -s /root/.m2/settings.xml clean package -DskipTests

# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port your app runs on
EXPOSE 8081

# Environment variable for OpenWeatherMap API key
ENV OWM_API_KEY="d32b8c8e2deb11f0c15f6b8347799b13"

# Optional: Persistent SQLite data volume
VOLUME ["/data"]

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
