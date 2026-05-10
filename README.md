# Smart Assignment Planner

Web app for students to manage courses and assignments, with a calendar view and dashboard. This repository contains a **Spring Boot** backend that serves the UI and JSON API, backed by **MySQL**.

## Prerequisites

| Requirement | Notes |
|-------------|--------|
| **Java 21** | Install a JDK (e.g. [Eclipse Temurin](https://adoptium.net/)). Verify: `java -version` |
| **MySQL 8** | Server running locally (default port **3306**) |

## One-time MySQL setup

Create a database and user that match the app configuration (or change `application.properties` to match your own credentials).

1. Start MySQL and connect as root (or another account that can create databases and users).

2. Run:

```sql
CREATE DATABASE IF NOT EXISTS smart_assignment_planner
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'sap_user'@'localhost' IDENTIFIED BY 'password123';
GRANT ALL PRIVILEGES ON smart_assignment_planner.* TO 'sap_user'@'localhost';
FLUSH PRIVILEGES;
```

Connection settings live in:

`backend/planner-backend/src/main/resources/application.properties`

On first startup with an **empty** database, the app seeds demo users and sample data (see `DataInitializer` in the code). Example logins after a fresh DB:

- **Admin:** `admin@planner.local` / `admin123`
- **Demo users:** passwords documented in the initializer (often `password123` for seeded student accounts)

You can also **register** a new account from the login page if registration is enabled.

## Run the application (Windows or macOS)

1. **Clone** this repository from GitHub and open a terminal in the project folder.

2. **Start MySQL** so it accepts connections on `localhost:3306`.

3. From the backend module directory, start Spring Boot:

   **Windows (PowerShell or Command Prompt)**

   ```bat
   cd backend\planner-backend
   .\gradlew.bat bootRun
   ```

   **macOS or Linux**

   ```bash
   cd backend/planner-backend
   chmod +x gradlew
   ./gradlew bootRun
   ```

4. Open a browser at:

   **http://localhost:8080**

   Use **`localhost`**, not `127.0.0.1`, so session cookies behave as intended.

5. **Stop** the server with `Ctrl+C` in the terminal.

## Project layout (high level)

- `backend/planner-backend/` — Gradle project, Spring Boot API and static UI under `src/main/resources/static/`
- `old_code/` — Legacy reference code (optional)

## Troubleshooting

- **Port 8080 in use:** Stop the other process or set `server.port` in `application.properties`.
- **Access denied for MySQL user:** Recreate the user/GRANT or align username/password with `application.properties`.
- **401 after login:** Use the same host you used to open the app (e.g. always `http://localhost:8080`).

---

*Originally developed as a class project; continued as part of a grant-funded effort.*
