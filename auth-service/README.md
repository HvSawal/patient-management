# 🔐 Auth Service

The **Auth Service** is a Spring Boot–based backend service responsible for **authentication** and **JWT token management** in the **Patient Management System**.  
It exposes REST endpoints for logging in and validating JWT tokens and is used by both clients and the API Gateway.

---

## ✨ Features

- User authentication using email and password.
- JWT token generation for authenticated users.
- JWT token validation for downstream services (via the API Gateway).
- User data stored in a PostgreSQL database (with initial seed data).
- Dockerized runtime for both the service and its database.

---

## 🔌 REST API Overview

By design, the Auth Service is typically accessed **through the API Gateway**, but it can also be called directly for testing.

Assuming the Auth Service is running on **`http://auth-service:4005`** inside Docker:

| Method | Endpoint                | Description                              |
|--------|-------------------------|------------------------------------------|
| `POST` | `/login`                | Authenticates a user and returns a JWT token. |
| `GET`  | `/validate` | Validates a JWT token and returns its validity. |

> Through the API Gateway on `http://localhost:4004`, these typically map to:
> - `POST http://localhost:4004/auth/login`
> - `GET  http://localhost:4004/auth/validate`

### Request / Response DTOs

#### `LoginRequestDTO`

```json
{
  "email": "user@example.com",
  "password": "plain-text-password"
}
```

Fields:

- `email` – User’s email address.
- `password` – User’s password (plain text in the request; compared using a `PasswordEncoder` on the server).

#### `LoginResponseDTO`

```json
{
  "token": "jwt-token-here"
}
```

Fields:

- `token` – A signed JWT token that can be used in the `Authorization` header to access protected endpoints via the API Gateway.

---

## 👤 User Model

The Auth Service manages a simple `User` model:

| Field      | Type   | Description                |
|------------|--------|----------------------------|
| `id`       | UUID   | Unique identifier for user |
| `email`    | String | Unique email address       |
| `password` | String | Encoded (hashed) password  |
| `role`     | String | User role (e.g., `ADMIN`)  |

Initial data is populated via `data.sql`:

```sql
-- Ensure the 'users' table exists
CREATE TABLE IF NOT EXISTS "users" (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

-- Insert the user if no existing user with the same id or email exists
INSERT INTO "users" (id, email, password, role)
SELECT '223e4567-e89b-12d3-a456-426614174006', 'testuser@test.com',
       '$2b$12$7hoRZfJrRKD2nIm2vHLs7OBETy.LWenXXMLKf99W8M4PUwO6KB7fu', 'ADMIN'
WHERE NOT EXISTS (
    SELECT 1
    FROM "users"
    WHERE id = '223e4567-e89b-12d3-a456-426614174006'
       OR email = 'testuser@test.com'
);
```

> The password stored is a **hashed value** (e.g., using BCrypt).  
> Authentication is performed using a `PasswordEncoder` to safely compare passwords.

---

## 🔑 JWT Handling

The Auth Service uses a utility (e.g., `JwtUtil`) to:

- **Generate tokens** after successful authentication.
- **Validate tokens** for subsequent requests (used by the API Gateway).

The cryptographic signing secret is injected via the `JWT_SECRET` environment variable.

> For security, avoid committing real secrets in source control.  
> Use strong, environment-specific secrets and manage them via environment variables or a secure vault.

---

## 🐳 Docker Setup (IntelliJ)

The Auth Service runs as a Docker container and uses a separate PostgreSQL database container.

> 💡 **IntelliJ Community Edition:**  
> Install the **“Docker”** plugin from the JetBrains Marketplace to use Docker run configurations.

### 1. Dockerfile Build Configuration (auth-service)

Dockerfile (simplified description):

- Multi-stage build using `maven:3.9.9-eclipse-temurin-21` as the builder.
- Builds the jar with `mvn clean package`.
- Uses `eclipse-temurin:21-jdk` as the runtime image.
- Copies the built jar to `/app/app.jar`.
- Exposes port `4005`.
- Entrypoint: `java -jar app.jar`.

**IntelliJ Docker run configuration:**
```text
- Type: Dockerfile
- Dockerfile: this module’s `Dockerfile`
- Name: auth-service
- Image tag: auth-service:latest
- Container name: auth-service
- Bind ports:
  4005:4005

- Environment variables:
  JWT_SECRET=d412da56cebb579959619e6967eb08da903c90d1aa65b31274d4d3dd6e577402
  SPRING_DATASOURCE_PASSWORD=password
  SPRING_DATASOURCE_URL=jdbc:postgresql://auth-service-db:5432/db
  SPRING_DATASOURCE_USERNAME=admin_user
  SPRING_JPA_HIBERNATE_DDL_AUTO=update
  SPRING_SQL_INIT_MODE=always

- Run options: --network internal
```

> Replace `JWT_SECRET` with a strong, environment-specific value in real deployments.

### 2. PostgreSQL Database Configuration (auth-service-db)

Create a Docker run configuration for the Auth Service database using the official PostgreSQL image.

**IntelliJ Docker run configuration:**
```text
- Type: Image (or Docker Image)
- Image ID or name: postgres:17.7
- Container name: auth-service-db
- Bind ports:
  5001:5432

- Bind mounts:
  ..\Patient Management\db_volumnes\auth-service-db:/var/lib/postgresql/data

  (Adjust the path as needed for your environment.)

- Environment variables:
  POSTGRES_DB=db
  POSTGRES_PASSWORD=password
  POSTGRES_USER=admin_user

- Run options: --network internal
```

> The `auth-service` container and `auth-service-db` share the same `internal` Docker network,  
> so the service can access the database at `jdbc:postgresql://auth-service-db:5432/db`.

### 3. Startup Order

1. **Start the Auth Service database** (`auth-service-db`).
2. **Start the Auth Service** (`auth-service`).
3. Ensure the **API Gateway** (`api-gateway`) is running so it can route `/auth/**` traffic to the auth-service.

---

## 🏃 Running the Service Without Docker

If you want to run the Auth Service directly on your machine:

### From the Module Directory

```bash
cd auth-service
mvn spring-boot:run
```

By default, configure `server.port=4005` so it matches the gateway configuration.

### Using the Built JAR

```bash
mvn clean package
java -jar target/auth-service-0.0.1-SNAPSHOT.jar
```

(Adjust the version suffix as needed, or use a wildcard such as `auth-service-*.jar`.)

---

## ⚙️ Configuration

Configuration is typically managed via `application.properties` plus environment variables:

- `server.port=4005`
- `spring.datasource.url=jdbc:postgresql://auth-service-db:5432/db`
- `spring.datasource.username=admin_user`
- `spring.datasource.password=password`
- `spring.jpa.hibernate.ddl-auto=update`
- `spring.sql.init.mode=always` (so `data.sql` runs on startup)
- `JWT_SECRET` – secret key used to sign and validate JWT tokens

> For security, treat `JWT_SECRET` and database credentials as sensitive and manage them appropriately.

---

## 🔭 Future Enhancements (Service-Level)

Possible future improvements for the Auth Service:

- Refresh tokens and token revocation.
- Role-based and permission-based authorization endpoints.
- User registration and profile management.
- Password reset and account lockout policies.
- Integration with external identity providers (OIDC, OAuth2, etc.).

---

## 🔗 Related

- Parent project: [`patient-management`](../README.md)
- API Gateway: [`api-gateway`](../api-gateway/README.md)
- Patient Service: [`patient-service`](../patient-service/README.md)
