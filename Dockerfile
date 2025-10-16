FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8081
ENV OWM_API_KEY="d32b8c8e2deb11f0c15f6b8347799b13"
ENTRYPOINT ["java", "-jar", "app.jar"]
