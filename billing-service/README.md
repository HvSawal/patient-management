# 💳 Billing Service

The **Billing Service** is a gRPC-based backend service that manages **billing accounts** as part of the broader **Patient Management System**.  
It exposes a single gRPC endpoint for now, allowing the **Patient Service** (or other clients) to create billing accounts for patients via **Protobuf-defined contracts**.

---

## ✨ Features

- gRPC server for billing-related operations
- `CreateBillingAccount` RPC to create a billing account for a patient
- Strongly-typed contracts using **Protocol Buffers (proto3)**
- Designed to be called from the `patient-service`
- **Dockerized runtime** exposing both HTTP and gRPC ports

---

## 🔗 gRPC Service Overview

- **Service name:** `BillingService`
- **RPC method:** `CreateBillingAccount`
- **gRPC endpoint (local):** `localhost:9002`

| RPC Method             | Request Type     | Response Type     | Description                          |
|------------------------|------------------|-------------------|--------------------------------------|
| `CreateBillingAccount` | `BillingRequest` | `BillingResponse` | Creates a billing account for a user |

### Request: `BillingRequest`

| Field       | Type   | Description                            |
|------------|--------|----------------------------------------|
| `patientId`| string | Identifier of the patient              |
| `name`     | string | Name of the patient                    |
| `email`    | string | Email of the patient                   |

### Response: `BillingResponse`

| Field        | Type   | Description                               |
|-------------|--------|-------------------------------------------|
| `accountId` | string | Unique identifier of the billing account  |
| `status`    | string | Status of the operation (e.g. `CREATED`)  |

---

## 🧪 gRPC Example (Postman)

You can call the service using **Postman gRPC** or any other gRPC client.

**Endpoint:**

```text
GRPC localhost:9002/BillingService/CreateBillingAccount
```

**Sample request payload:**

```json
{
  "patientId": "12344",
  "name": "John Doe",
  "email": "john.doe@example.com"
}
```

The response will be a `BillingResponse` message that includes an `accountId` and `status`.

---

## 🐳 Docker Setup (IntelliJ)

This service can be containerized using Docker.  
The examples below assume you are using **IntelliJ IDEA** with Docker run configurations.

> 💡 **IntelliJ Community Edition:**  
> You need to install the **“Docker”** plugin from the JetBrains Marketplace to use Docker run configurations.

### 1. Dockerfile Build Configuration (billing-service)

Dockerfile (simplified description):

- Multi-stage build using `maven:3.9.9-eclipse-temurin-21` as the **builder**
- Runs `mvn dependency:go-offline -B` to prefetch dependencies
- Builds the jar with `mvn clean package`
- Uses `eclipse-temurin:21-jdk` as the **runtime** image
- Copies the built jar to `/app/app.jar`
- Exposes ports:
    - `4001` – HTTP / management port
    - `9002` – gRPC port
- Entrypoint: `java -jar app.jar`

**IntelliJ Docker run configuration:**

```text
- Type: Dockerfile
- Dockerfile: this module’s `Dockerfile`
- Image tag: billing-service:latest
- Container name: billing-service
- Bind ports:
    4001:4001
    9002:9002

- Run options: --network internal
```

> The `--network internal` allows this container to communicate with other containers (for example, `patient-service`) on the same custom Docker network using their container names.

After the container is running:

- gRPC endpoint in Docker: `localhost:9002/BillingService/CreateBillingAccount`
- HTTP port: `http://localhost:4001`

---

## 🏃 Running the Service Without Docker

If you want to run the service directly on your machine:

### From the Module Directory

```bash
cd billing-service
mvn spring-boot:run
```

By default, the gRPC server listens on **`localhost:9002`** (_adjust according to your gRPC server configuration_).

### Using the Built JAR

```bash
mvn clean package
java -jar target/billing-service-0.0.1-SNAPSHOT.jar
```

---

## ⚙️ Configuration

Configuration is managed via [`application.properties`](https://github.com/HvSawal/patient-management/blob/main/billing-service/src/main/resources/application.properties) in `src/main/resources`.

Common Properties:
- `server.port` - HTTP request (default: `4001`)
- `grpc.server.port` - gRPC protobuf request (default: `9002`)

---


## 🔭 Future Enhancements (Service-Level)

Planned/possible future improvements for the Billing Service:

- Additional RPC methods (e.g., `GetBillingAccount`, `UpdateBillingAccount`, `ListBillingAccounts`)
- Persisting billing data to a dedicated billing database
- Integration with payment gateways or invoicing systems
- Enhanced error handling and standardized status codes
- Metrics, tracing, and logging tuned for gRPC traffic

---

## 🔗 Related

- Parent project: [`patient-management`](../README.md)
- Patient service: [`patient-service`](../patient-service/README.md)
