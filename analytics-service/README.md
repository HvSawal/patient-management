# 📈 Analytics Service

The **Analytics Service** is a Spring Boot–based backend service that consumes **patient-related events** from Kafka as part of the broader **Patient Management System**.  
It listens to Protobuf-encoded `PatientEvent` messages produced by the `patient-service` and is designed to power future reporting, monitoring, and analytics features.

---

## ✨ Features

- **Kafka consumer** for patient events
- Consumes Protobuf-encoded `PatientEvent` messages
- Runs as a standalone Spring Boot microservice
- **Dockerized runtime** with configurable Kafka bootstrap servers

---

## 📡 Kafka Consumption Overview

The Analytics Service acts as a **Kafka consumer** that subscribes to one or more patient-related topics.

A typical `PatientEvent` contains:

- `patientId` – the ID of the patient
- `name` – the patient’s name
- `email` – the patient’s email
- `event_type` – the type of event (for example, `CREATED`)

These messages are published by the `patient-service` using a Protobuf encoder and consumed by this service using a **byte-array value deserializer**, then decoded into the `PatientEvent` model.

---

## ⚙️ Kafka Consumer Configuration

The Kafka consumer is configured via `application.properties`:

```properties
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.ByteArrayDeserializer

spring.kafka.consumer.auto-offset-reset=earliest

server.port=4002
```

- Keys are deserialized as **Strings**.
- Values are deserialized as **raw byte arrays**, which correspond to the Protobuf-encoded `PatientEvent` payloads.
- `auto-offset-reset=earliest` ensures the consumer starts from the beginning of the topic if no committed offsets exist.
- The service exposes an HTTP port on **`4002`** (for health checks, future endpoints, etc.).

When running in Docker, the Kafka bootstrap servers are injected via:

```text
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
```

This points the consumer to the **Kafka broker** running in Docker on the `internal` network under the hostname `kafka`.

---

## 🐳 Docker Setup (IntelliJ)

This service is packaged and run in Docker, alongside other services and Kafka.

> 💡 **IntelliJ Community Edition:**  
> You need to install the **“Docker”** plugin from the JetBrains Marketplace to use Docker run configurations.

### Dockerfile Build Configuration (analytics-service)

Dockerfile (simplified description):

- Multi-stage build using `maven:3.9.9-eclipse-temurin-21` as the builder
- Runs `mvn clean package` to build the JAR
- Uses `eclipse-temurin:21-jdk` as the runtime image
- Copies the built JAR to `/app/app.jar`
- Exposes port `4002`
- Entrypoint: `java -jar app.jar`

**IntelliJ Docker run configuration:**
```text
- Type: Dockerfile
- Dockerfile: this module’s `Dockerfile`
- Name: analytics-service
- Image tag: analytics-service:latest
- Container name: analytics-service
- Bind ports:
  4002:4002

- Environment variables:
  SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092

- Run options: --network internal
```

> The `--network internal` option puts this container on the same Docker network as:
> - The Kafka broker (`kafka` at `kafka:9092`)
> - Other services (such as `patient-service`) that publish events.

Once running, the Analytics Service will automatically connect to Kafka and start consuming messages from the configured topic(s).

---

## 🏃 Running the Service Without Docker

If you want to run the service directly on your machine:

### From the Module Directory

```bash
cd analytics-service
mvn spring-boot:run
```

By default, the application starts on **`http://localhost:4002`** (for health checks or future endpoints).

Make sure you have a Kafka broker running and that `spring.kafka.bootstrap-servers` (or `SPRING_KAFKA_BOOTSTRAP_SERVERS`) is configured appropriately.

### Using the Built JAR

```bash
mvn clean package
java -jar target/analytics-service-0.0.1-SNAPSHOT.jar
```

(Adjust the version suffix as needed, or use a wildcard such as `analytics-service-*.jar`.)

---

## 🔧 Configuration

Key configuration areas:

- **Kafka Consumer:**
    - `spring.kafka.consumer.key-deserializer`
    - `spring.kafka.consumer.value-deserializer`
    - `spring.kafka.consumer.auto-offset-reset`
    - `SPRING_KAFKA_BOOTSTRAP_SERVERS` (environment variable, especially for Docker)

- **Server:**
    - `server.port=4002`

Configuration is typically managed via `application.properties` plus environment variables for environment-specific details (like Kafka bootstrap servers).

---

## 🔭 Future Enhancements (Service-Level)

Potential future improvements for the Analytics Service:

- Persisting consumed events to a data store for reporting and dashboards
- Aggregations and metrics (e.g., number of patients created per day)
- Exposing REST endpoints for analytics queries
- Integration with visualization tools or a dedicated UI
- More robust error handling, retries, and dead-letter topics
- Structured logging, tracing, and metrics for observability

---

## 🔗 Related

- Parent project: [`patient-management`](../README.md)
- Producer service: [`patient-service`](../patient-service/README.md) – publishes `PatientEvent` messages to Kafka
