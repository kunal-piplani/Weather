# Weather App — Spring Boot + SQLite + Docker

A lightweight **Spring Boot web application** that lets users sign up, log in, and search weather information by city using the **OpenWeatherMap API**.  
The app stores user search history in **SQLite**, allowing users to view or clear their recent searches.

---

## Features

-  User authentication (Sign-up / Login / Logout)
-  Fetch real-time weather data via OpenWeatherMap API
-  View last 5 recent searches per user
-  Clear search history (with success messages)
-  Uses SQLite (file-based database — zero setup!)
-  Fully Dockerized for one-command deployment

---

## Tech Stack

| Component | Description |
|------------|-------------|
| **Backend** | Spring Boot 3.3.x |
| **Database** | SQLite |
| **Frontend** | Thymeleaf + HTML/CSS |
| **Security** | Spring Security + BCrypt |
| **API** | OpenWeatherMap (REST) |
| **Build Tool** | Maven |
| **Container** | Docker (Java 21 Alpine) |

---

## Prerequisites

Before running locally or in Docker:
- [Java 21+](https://adoptium.net/)
- [Maven 3.9+](https://maven.apache.org/)
-  [Docker](https://docs.docker.com/get-docker/)
- An [OpenWeatherMap API Key](https://openweathermap.org/api)

---

## Configuration

### `src/main/resources/application.yml`

```yaml
server:
  port: 8081

owm:
  api:
    key: ${OWM_API_KEY:}  # Loaded from environment variable

spring:
  datasource:
    url: jdbc:sqlite:/data/weather.db
    driver-class-name: org.sqlite.JDBC

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate.dialect: org.brightedge.config.SQLiteDialect
```

---

## Docker Setup

### Build Docker Image
```bash
docker build -t weather-app .
```

###  Run Container
```bash
docker run -d   -p 8081:8081   -v $(pwd)/data:/data --name weather-app   weather-app
```
if key not Worked Please generate from the UI and run  this command 
```bash
docker run -d   -p 8081:8081   -v $(pwd)/data:/data   -e OWM_API_KEY="YOUR_OPENWEATHERMAP_API_KEY"   --name weather-app   weather-app
```

SQLite database persists in `./data/weather.db`  
Application accessible at [http://localhost:8081](http://localhost:8081)



## Authentication Flow

1. Visit `/signup` → Create a new user
2. Login at `/login`
3. Search for weather → results + history saved per user
4. Clear search history anytime
5. Logout from top-right corner

---

##  Maintenance

To open your SQLite DB:
```bash
sqlite3 data/weather.db
```

View users:
```sql
SELECT * FROM users;
```

Remove duplicate usernames:
```sql
DELETE FROM users
WHERE username = 'someuser'
AND id NOT IN (SELECT MIN(id) FROM users WHERE username = 'someuser');
```

---

## Troubleshooting

| Issue | Cause | Solution |
|--------|--------|----------|
| `city not found` | Invalid city name | Check spelling / API key |
| `403 Forbidden` | Missing login | Log in first |
| `No EntityManager` | Missing `@Transactional` | Added in `SearchHistoryService` |
| DB not persisting | Volume not mounted | Ensure `-v $(pwd)/data:/data` is set |

---

## License
License © 2025 — Developed by **Kunal Piplani**
