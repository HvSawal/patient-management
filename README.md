# 🏥 Patient Management System 🏥

A modular, **production-inspired** backend system built with **Spring Boot** and a **microservice-style** architecture for managing patients and related healthcare operations.  
It showcases a complete flow with **API Gateway**, **JWT-based auth**, **REST + gRPC services**, **Kafka-based events**, **analytics**, **PostgreSQL**, **Docker**, and **LocalStack** for AWS emulation.

---

## 🚀 Tech Stack & Tooling

<p align="left">
  <!-- Languages & Frameworks -->
  <img src="https://img.shields.io/badge/Java-21+-red?logo=openjdk" alt="Java" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?logo=springboot" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Spring%20Cloud-Gateway-6DB33F?logo=spring" alt="Spring Cloud Gateway" />
  <img src="https://img.shields.io/badge/Maven-Build-blue?logo=apachemaven" alt="Maven" />
  <!-- Infrastructure & Messaging -->
  <img src="https://img.shields.io/badge/Docker-Containerization-2496ED?logo=docker" alt="Docker" />
  <img src="https://img.shields.io/badge/PostgreSQL-Database-336791?logo=postgresql" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/Apache%20Kafka-Streaming-231F20?logo=apachekafka" alt="Kafka" />
  <img src="https://img.shields.io/badge/gRPC-Protobuf-1c5d99?logo=google" alt="gRPC" />
  <!-- Cloud & Testing -->
  <img src="https://img.shields.io/badge/AWS-LocalStack-FF9900?logo=amazonaws" alt="AWS / LocalStack" />
  <img src="https://img.shields.io/badge/JUnit-Testing-25A162?logo=junit5" alt="JUnit" />
  <img src="https://img.shields.io/badge/OpenAPI-Swagger-6BA539?logo=swagger" alt="OpenAPI/Swagger" />
</p>

> ℹ️ **Note:** This is an evolving system. Not every integration is feature-complete yet, but the core building blocks are wired together and running end-to-end in Docker, mirroring how they would be deployed on AWS.

---

## 🎯 Project Goals

The Patient Management System is designed to:

- Demonstrate a **realistic microservice ecosystem**:
    - API gateway, auth, domain services, billing, analytics.
    - REST for external clients, gRPC for internal calls, Kafka for events.
- Serve as a **playground for modern backend patterns**:
    - Service-to-service communication (gRPC + HTTP).
    - Event-driven architecture with **Kafka producers/consumers**.
    - JWT-secured APIs via a **gateway**.
- Model a **cloud-native AWS deployment**:
    - Services mapped conceptually to **ECS** tasks behind load balancers.
    - **MSK**-style Kafka cluster for messaging.
    - **RDS**-like managed PostgreSQL databases.
    - All backend components living inside a **VPC**, with the client entering via the gateway/load balancer.
    - Infrastructure interactions (ECS, EC2, MSK, RDS, ELB, API Gateway Management API, CloudFormation) simulated locally using **LocalStack**.
- Act as a **portfolio-quality reference**:
    - Clean separation of concerns per service.
    - Clear data flow and documentation that’s easy to reason about.
    - Recruiter-friendly overview of how you design and integrate systems in a cloud context.

---

## 🧱 Project Structure

The repository is a **Maven multi-module** project with multiple Spring Boot services and supporting infrastructure.

```text
patient-management/
├── api-gateway/          # Single entry point, routing + JWT validation
├── auth-service/         # Authentication + JWT token issuance/validation
├── patient-service/      # Core patient CRUD + Kafka producer + gRPC client
├── billing-service/      # gRPC server for billing account creation
├── analytics-service/    # Kafka consumer for patient events
└── README.md             # This file
```

Supporting infrastructure runs as Docker containers (configured via IntelliJ Docker run configs), mirroring an AWS-style deployment:

- **PostgreSQL (RDS-style)**
    - `patient-service-db` – database for patient-service.
    - `auth-service-db` – database for auth-service.
- **Kafka ecosystem (MSK-style)**
    - `kafka` – Apache Kafka broker for events.
    - `kafka-ui` – Web UI for inspecting topics and messages.
- **API & Networking (ECS / ELB / VPC-style)**
    - All services run in containers on a shared Docker network, representing a private **VPC**.
    - `api-gateway` is the single ingress point (akin to an internet-facing load balancer / API Gateway in front of ECS services).
- **AWS Emulation (LocalStack)**
    - LocalStack is used to emulate AWS services such as:
        - **ECS / EC2**
        - **MSK**
        - **RDS**
        - **ELB / Load Balancing**
        - **API Gateway Management API**
        - **CloudFormation**
    - This allows the architecture and integrations to be designed as if they were running fully on AWS, while remaining local and self-contained.

### 🔄 High-Level Flow

```text
End User / Client
        |
        v
  ┌───────────────┐       JWT-secured routes        ┌─────────────────┐
  │APIGateway(ECS)│  <-------------------------->   │Auth Service(ECS)│
  └───────────────┘                                 └─────────────────┘
            |
            |  /api/patients/**
            v
  ┌────────────────────┐               ┌────────────────────┐
  │Patient Service(ECS)│  --(gRPC)-->  │Billing Service(ECS)│
  │  (Kafka producer)  │               └────────────────────┘
  └────────────────────┘               
            |
            |  Kafka (MSK): PatientEvent (Protobuf)
            v
  ┌──────────────────────┐
  │Analytics Service(ECS)│
  │ (Kafka consumer)     │
  └──────────────────────┘
```

- **API Gateway**:
    - Routes `/api/patients/**` to `patient-service` (JWT-protected).
    - Routes `/auth/**` to `auth-service` for login/token validation.
    - Routes `/api-docs/patients` and `/api-docs/auth` to each service’s OpenAPI docs.
- **Patient Service**:
    - Handles RESTful patient CRUD.
    - On patient creation:
        - Calls **Billing Service** over gRPC to create a billing account.
        - Publishes a **Protobuf-encoded `PatientEvent`** to Kafka.
- **Analytics Service**:
    - Consumes `PatientEvent` messages from Kafka for downstream analytics.
- **Auth Service**:
    - Issues and validates JWTs, used by clients and the API Gateway.

---

## 📌 Status

> 🟢 **Current state:**
> - `api-gateway` routes traffic to `auth-service` and `patient-service` and performs JWT validation on protected routes.
> - `auth-service` authenticates users against PostgreSQL, issues JWTs, and supports token validation.
> - `patient-service` provides REST CRUD for patients, documents APIs via Swagger, publishes Kafka events, and calls `billing-service` over gRPC.
> - `billing-service` exposes a gRPC endpoint to create billing accounts for patients.
> - `analytics-service` consumes patient events from Kafka for future reporting/analytics use cases.
> - PostgreSQL, Kafka, Kafka UI, and LocalStack are wired in via Docker for a realistic local environment.

The system already demonstrates:

- A **full request lifecycle** from client → gateway → auth/patient/billing/analytics.
- Multiple integration styles (REST, gRPC, Kafka events).
- Containerized, networked microservices with separate databases.

Further refinements (more AWS usage via LocalStack, richer analytics, more services) will be added iteratively.

---

## 🧑‍💻 Author / Maintainer

- **Project Owner:** *Harshvardhan Sawal*
- Designed as a showcase of modern backend engineering practices – open to future extensions, refactors, and experimentation. 🧪⚙️
