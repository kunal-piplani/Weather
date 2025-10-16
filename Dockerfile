# ---------- Stage 1: Build ----------
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# 🔧 Add reliable Maven Central mirror to bypass connection timeouts
RUN mkdir -p /root/.m2 && echo '\
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" \
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" \
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 \
          https://maven.apache.org/xsd/settings-1.0.0.xsd"> \
  <mirrors> \
    <mirror> \
      <id>maven-default</id> \
      <mirrorOf>*</mirrorOf> \
      <url>https://repo1.maven.org/maven2/</url> \
    </mirror> \
    <mirror> \
      <id>aliyun</id> \
      <mirrorOf>*</mirrorOf> \
      <url>https://maven.aliyun.com/repository/public</url> \
    </mirror> \
  </mirrors> \
</settings>' > /root/.m2/settings.xml

# Copy everything
COPY . .

# Clean and build using the mirror
RUN mvn -s /root/.m2/settings.xml clean package -DskipTests

# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copy JAR from build
COPY --from=build /app/target/*.jar app.jar

# Expose the port
EXPOSE 8081

# Optional API key for OpenWeatherMap
ENV OWM_API_KEY="d32b8c8e2deb11f0c15f6b8347799b13"

# Optional: persistent SQLite data volume
VOLUME ["/data"]

# Start the app
ENTRYPOINT ["java", "-jar", "app.jar"]
