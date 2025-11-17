# 🚪 API Gateway

The **API Gateway** is the single entry point into the **Patient Management System**.  
It routes incoming HTTP traffic to the appropriate backend services (patient-service, auth-service) and applies **JWT-based authentication** for protected routes.

---

## ✨ Features

- Centralized routing for backend services.
- JWT validation for protected APIs (via a custom gateway filter).
- Path rewriting and prefix stripping to keep downstream services clean.
- Simple pass-through for service-specific Swagger / OpenAPI docs.

---

## 🔀 Routing Overview

The API Gateway currently exposes the following external routes (assuming the gateway is running on **`http://localhost:4004`**):

### 1. Patient API (Protected)

- **External path:** `http://localhost:4004/api/patients/**`
- **Forwarded to:** `http://patient-service:4000/**`
- **Behavior:**
    - Strips the `/api` prefix before forwarding.
    - Applies **JWT validation** to ensure the caller is authenticated.

> Example:
> - `GET http://localhost:4004/api/patients` → `GET http://patient-service:4000/patients`
> - `POST http://localhost:4004/api/patients` → `POST http://patient-service:4000/patients`

### 2. Auth API (Public)

- **External path:** `http://localhost:4004/auth/**`
- **Forwarded to:** `http://auth-service:4005/**`
- **Behavior:**
    - Strips the `/auth` prefix before forwarding.
    - Used by clients to obtain and validate JWT tokens.

> Example:
> - `POST http://localhost:4004/auth/login` → `POST http://auth-service:4005/login`
> - `GET  http://localhost:4004/auth/validate` → `GET http://auth-service:4005/validate` (or equivalent validation endpoint)

### 3. Swagger / API Docs – Patient Service

- **External path:** `http://localhost:4004/api-docs/patients`
- **Forwarded to:** `http://patient-service:4000/v3/api-docs`
- **Behavior:**
    - Rewrites the path from `/api-docs/patients` to `/v3/api-docs` on the patient-service.

### 4. Swagger / API Docs – Auth Service

- **External path:** `http://localhost:4004/api-docs/auth`
- **Forwarded to:** `http://auth-service:4005/v3/api-docs`
- **Behavior:**
    - Rewrites the path from `/api-docs/auth` to `/v3/api-docs` on the auth-service.

---

## 🔐 JWT Validation

The API Gateway uses a custom **JWT validation filter** to secure selected routes:

- A custom filter (e.g., `JwtValidationGatewayFilterFactory`) integrates with the gateway.
- For protected routes (such as `/api/patients/**`), the filter:
    - Extracts the JWT from the incoming request (typically from the `Authorization` header).
    - Validates the token (delegating to the auth-service using `AUTH_SERVICE_URL`).
    - Rejects unauthorized or invalid tokens with an appropriate HTTP status (e.g., `401 Unauthorized`).

> The gateway also uses a custom exception type (e.g., `JwtValidationException`) to handle unauthorized access in a consistent way.

---

## 🐳 Docker Setup (IntelliJ)

The API Gateway is designed to run as a Docker container alongside other services on a shared Docker network.

> 💡 **IntelliJ Community Edition:**  
> Install the **“Docker”** plugin from the JetBrains Marketplace to use Docker run configurations.

### Dockerfile Build Configuration (api-gateway)

Dockerfile (simplified description):

- Multi-stage build using `maven:3.9.9-eclipse-temurin-21` as the builder.
- Builds the jar with `mvn clean package`.
- Uses `eclipse-temurin:21-jdk` as the runtime image.
- Copies the built jar to `/app/app.jar`.
- Exposes port `4004`.
- Entrypoint: `java -jar app.jar`.

**IntelliJ Docker run configuration:**
```text
- Type: Dockerfile
- Dockerfile: this module’s `Dockerfile`
- Name: api-gateway
- Image tag: api-gateway:latest
- Container name: api-gateway
- Bind ports:
  4004:4004

- Environment variables:
  AUTH_SERVICE_URL=http://auth-service:4005

- Run options: --network internal
```

> The `--network internal` option puts the API Gateway on the same Docker network as:
> - `patient-service` (e.g., `http://patient-service:4000`)
> - `auth-service` (e.g., `http://auth-service:4005`)
> - Kafka, databases, and other services (if needed).

---

## 🏃 Running the Service Without Docker

If you want to run the API Gateway directly on your machine:

### From the Module Directory

```bash
cd api-gateway
mvn spring-boot:run
```

By default, the gateway will start on **`http://localhost:4004`** (assuming `server.port=4004`).

### Using the Built JAR

```bash
mvn clean package
java -jar target/api-gateway-0.0.1-SNAPSHOT.jar
```

(Adjust the version suffix as needed, or use a wildcard such as `api-gateway-*.jar`.)

---

## ⚙️ Configuration

Configuration is managed via `application.yml`, plus environment variables:

- `server.port` – Gateway port (default: `4004`).
- `spring.cloud.gateway.*` – Route, predicate, and filter definitions.
- `AUTH_SERVICE_URL` – Base URL of the auth-service used by the JWT validation filter.

Keep secrets and environment-specific URLs in environment variables or external config, rather than hard-coding them.

---

## 🔭 Future Enhancements (Service-Level)

Potential future improvements for the API Gateway:

- Rate limiting / throttling.
- Request/response logging and correlation IDs.
- Global error handling and standardized error responses.
- API versioning and route grouping.
- Role-based access control (RBAC) on protected routes.

---

## 🔗 Related

- Parent project: [`patient-management`](../README.md)
- Patient service: [`patient-service`](../patient-service/README.md)
- Auth service: [`auth-service`](../auth-service/README.md)
