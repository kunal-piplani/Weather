FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy the entire project (pom.xml + src)
COPY . .

# Build the Spring Boot JAR inside Docker
RUN mvn clean package -DskipTests

# ---------- Stage 2: Run the App ----------
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/weatherapp-0.0.1-SNAPSHOT.jar app.jar

# Create data folder (for SQLite persistence)
# Create writable /data directory for SQLite
RUN mkdir -p /data && chmod 777 /data
VOLUME /data

# Expose port
EXPOSE 8081

# Add API key (for demo only; in prod, use docker run -e)
ENV OWM_API_KEY="d32b8c8e2deb11f0c15f6b8347799b13"

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
