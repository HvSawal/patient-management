# 🩺 Patient Service

The **Patient Service** is a Spring Boot–based REST API that manages **patient records** as part of the broader **Patient Management System**.  
It currently exposes **CRUD operations** for patient entities and acts as the foundational service other modules (e.g., billing, auth, gateway) will integrate with.

---

## ✨ Features

- **CRUD operations:**
  - Create new patients
  - Retrieve a single patient by ID
  - List all patients
  - Update existing patients
  - Delete patients by ID
- **Interactive API documentation via Swagger / OpenAPI**
- **Dockerized runtime with PostgreSQL database**

---

## 🧬 Patient Domain Model

A typical `Patient` entity contains:

| Field           | Type       | Description                          |
|----------------|------------|--------------------------------------|
| `id`           | UUID       | Unique identifier for the patient    |
| `firstName`    | String     | Patient’s first name                 |
| `lastName`     | String     | Patient’s last name                  |
| `email`        | String     | Unique email address                 |
| `address`      | String     | Mailing address                      |
| `dateOfBirth`  | LocaleDate | Patient’s date of birth              |
| `registeredDate` | LocaleDate       | Date when the patient registered     |

> ℹ️ The table and some seed data can be initialized via `data.sql` when running locally.

---

## 🔗 REST API Overview

> **Base URL:** `http://localhost:4000/api/patients`

| Method   | Endpoint                  | Description                    |
|----------|---------------------------|--------------------------------|
| `POST`   | `/api/patients`           | Create a new patient           |
| `GET`    | `/api/patients/{id}`      | Get a patient by ID            |
| `GET`    | `/api/patients`           | List all patients              |
| `PUT`    | `/api/patients/{id}`      | Update an existing patient     |
| `DELETE` | `/api/patients/{id}`      | Delete a patient by ID         |

---

## 📘 API Documentation (Swagger / OpenAPI)

Once the application is running, you can explore and test the APIs via Swagger UI:

- Swagger UI (common defaults – update based on your setup):
    - `http://localhost:4000/swagger-ui.html`
    - or `http://localhost:4000/swagger-ui/index.html`

- OpenAPI JSON:
    - `http://localhost:4000/v3/api-docs`

Use this UI to:
- View all available endpoints
- Inspect request/response models
- Execute API calls directly from the browser

---

## 🧪 REST API Examples

### Example 1 : Create Patient

```bash
curl -X POST http://localhost:4000/api/patients \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "address": "123 Main St, Springfield",
    "dateOfBirth": "1985-06-15",
    "registeredDate": "2024-01-10"
  }'
```

### Example 2 : Get All Patient

```bash
curl -X GET http://localhost:4000/api/patients
```

### Example 3 : Get Patient by ID

```bash
curl -X GET http://localhost:4000/api/patients/123e4567-e89b-12d3-a456-426614174000
```

### Example 4 : Update Patient

```bash
curl -X PUT http://localhost:4000/api/patients/123e4567-e89b-12d3-a456-426614174000 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Johnathan",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "address": "456 Elm St, Springfield",
    "dateOfBirth": "1985-06-15"
  }'
```

### Example 5 : Delete Patient by ID

```bash
curl -X DELETE http://localhost:4000/api/patients/123e4567-e89b-12d3-a456-426614174000
```
**NOTE: Replace the above ```ID``` numbers with your specific UUID

---

## 🐳 Docker Setup (IntelliJ)

This project includes a Dockerized setup for the **patient-service** and a **PostgreSQL** database.  
The examples below assume you are using **IntelliJ IDEA** with Docker run configurations.

> 💡 **IntelliJ Community Edition:**  
> You need to install the **“[Docker](https://www.jetbrains.com/help/idea/docker.html)”** plugin from the JetBrains Marketplace to use Docker run configurations.

### 1. Dockerfile Build Configuration (patient-service)

[Dockerfile](https://github.com/HvSawal/patient-management/blob/main/patient-service/Dockerfile) (simplified description):

- Multi-stage build using `maven:3.9.9-eclipse-temurin-21` as builder
- Builds the jar with `mvn clean package`
- Uses `eclipse-temurin:21-jdk` as the runtime image
- Copies the built jar to `/app/app.jar`
- Exposes port `4000`
- Entrypoint: `java -jar app.jar`

**IntelliJ Docker run configuration:**
```
- Type: Dockerfile
- Dockerfile: this module’s `Dockerfile`
- Container name: `patient-service`
- Bind ports: `4000:4000`

- Environment variables:
  SPRING_DATASOURCE_PASSWORD=password
  SPRING_DATASOURCE_URL=jdbc:postgresql://patient-service-db:5432/db
  SPRING_DATASOURCE_USERNAME=admin_user
  SPRING_JPA_HIBERNATE_DDL_AUTO=update
  SPRING_SQL_INIT_MODE=always

- Run options: --network internal
```

> The `--network internal` ensures the application container can reach the database container via the hostname `patient-service-db`.

### 2. PostgreSQL Database Configuration (patient-service-db)

Create a second Docker run configuration for the database using the official PostgreSQL image.

**IntelliJ Docker run configuration:**
```
- Type: Image (or Docker Image)
- Image ID or name: `postgres:17.7`
- Container name: `patient-service-db`
- Bind ports: `5000:5432`  
  (Allows you to connect from the host via `localhost:5000`, while the container listens on `5432`.)
- Bind mounts:
  {PROJECT_DIRECTORY}\db_volumnes\patient-service-db:/var/lib/postgresql/data

  (Adjust the path as needed for your environment.)

- Environment variables:
  POSTGRES_USER=admin_user
  POSTGRES_PASSWORD=password
  POSTGRES_DB=db

- Run options: --network internal
```

> Both containers (`patient-service` and `patient-service-db`) share the same `internal` Docker network so that the application can use `jdbc:postgresql://patient-service-db:5432/db` as its datasource URL.

### 3. Startup Order

1. **Start the PostgreSQL container** (`patient-service-db`) first.
2. Once the DB container is healthy and running, **start the patient-service container**.

After both are running:

- API base URL in Docker: `http://localhost:4000/api/patients`
- Swagger UI in Docker: `http://localhost:4000/swagger-ui.html`

---

## 🏃 Running the Service Without Docker

If you want to run the service directly on your machine:

### From the Module Directory

```bash
cd patient-service
mvn spring-boot:run
```

By default, the application starts on **`http://localhost:4000`**.

### Using the Built JAR

```bash
mvn clean package
java -jar target/patient-service-*.jar
```

---

## ⚙️ Configuration

Configuration is managed via [`application.properties`](https://github.com/HvSawal/patient-management/blob/main/patient-service/src/main/resources/application.properties).

Common properties:

- `server.port` – HTTP port (default: `4000`)
- `spring.datasource.*` – Database connection details (overridden by Docker env vars in containers, check the commented section in application.properties)
- `spring.jpa.*` – JPA & Hibernate settings
- Swagger / OpenAPI configuration

For local development, you can use:
- `data.sql` for inserting initial patient records

---

## 🧪 Testing

Run tests with:

```bash
mvn test
```

Planned enhancements:

- REST-level integration tests
- Database-backed integration tests (e.g., PostgreSQL via Docker)
- Contract tests once other services (billing, auth, etc.) are added

---

## 🔭 Future Enhancements (Service-Level)

At the **patient-service** level, future improvements may include:

- Richer request validation with `jakarta.validation`
- Standardized error handling / problem details
- Extended OpenAPI metadata (tags, examples, versioning)
- Security integration with a dedicated auth/gateway layer
- Improved logging, metrics, and tracing

---

## 🔗 Related

- Parent project: [`patient-management`](../README.md)
