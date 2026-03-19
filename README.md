# HelpHub Backend API

This project is the backend service for HelpHub, built with **Spring Boot 3.5.11**.

## Tech Stack
- **Java 21**
- **Spring Boot 3.5.11**
- **Spring Data JPA** (PostgreSQL)
- **Spring Security & JWT**
- **Springdoc OpenAPI (Swagger)** - Accessible at `/v3/api-docs` and `/swagger-ui/index.html`
- **Lombok**
- **Liquibase**

## Getting Started

### Prerequisites
- JDK 21
- PostgreSQL
- Maven (or use the provided `./mvnw` wrapper)

### Setup and Run
1.  **Clone the repository**.
2.  **Configure Database**: Update `src/main/resources/application.properties` with your PostgreSQL credentials.
3.  **Run the application**:
    ```bash
    ./mvnw spring-boot:run
    ```

### Docker Setup
You can also run the application and its database using Docker.

1.  **Prerequisites**: Ensure you have Docker and Docker Compose installed.
2.  **Options to Run**:
    - **Develop Mode (with Watch)**: Recommended for development! Automatically rebuilds when you change files.
      ```bash
      docker compose up --build --watch
      ```
    - **Standard Run**:
      ```bash
      docker compose up --build --watch
      ```
3.  **Access the application**:
    - Backend API: [http://localhost:8080](http://localhost:8080)
    - Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
    - PostgreSQL: `localhost:5433` (Dockerized) | `localhost:5432` (Local)
      (Database credentials: `postgres` / `root`)

> [!NOTE]
> The external port for the Docker database is set to **5433** to avoid conflicts with your local PostgreSQL (running on 5432).

## Development Features

### Hot Reloading (DevTools)
We have enabled **Spring Boot DevTools** for faster development.
- When you make changes to Java files (`.java`), you need to **recompile** the file (e.g., in VS Code, Intellij, or via terminal) for the context to restart.
- Static resources and templates are updated automatically without a full restart.
- LiveReload is enabled; use a browser extension to auto-refresh the page on changes.

### API Standards
All API responses follow a generic wrapper structure:
```json
{
  "status": true,
  "status_code": "SUCCESS",
  "message": "Description",
  "data": { ... },
  "errors": { ... }
}
```

### Swagger Documentation
Swagger UI can be accessed at:
[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
