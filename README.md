# 🏥 Patient Management System

A modular **Spring Boot**–based backend system for managing patients and related healthcare operations.  
This repository is structured as a **Maven multi-module project**, starting with a core `patient-service` for basic patient CRUD operations, and designed to grow into a richer ecosystem over time (billing, auth, API gateway, etc.).

---

## 🚀 Tech Stack & Tooling

<p align="left">
  <!-- Languages & Frameworks -->
  <img src="https://img.shields.io/badge/Java-21+-red?logo=openjdk" alt="Java" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?logo=springboot" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Maven-Build-blue?logo=apachemaven" alt="Maven" />
  <!-- Infrastructure & Messaging -->
  <img src="https://img.shields.io/badge/Docker-Containerization-2496ED?logo=docker" alt="Docker" />
  <img src="https://img.shields.io/badge/PostgreSQL-Database-336791?logo=postgresql" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/Apache%20Kafka-Streaming-231F20?logo=apachekafka" alt="Kafka" />
  <img src="https://img.shields.io/badge/gRPC-Protobuf-1c5d99?logo=google" alt="gRPC" />
  <!-- Cloud & Testing -->
  <img src="https://img.shields.io/badge/AWS-Cloud-FF9900?logo=amazonaws" alt="AWS" />
  <img src="https://img.shields.io/badge/JUnit-Testing-25A162?logo=junit5" alt="JUnit" />
  <img src="https://img.shields.io/badge/OpenAPI-Swagger-6BA539?logo=swagger" alt="OpenAPI/Swagger" />
</p>

> ℹ️ **Note:** Not all integrations are implemented yet. This project is intentionally evolving and will gradually incorporate more infrastructure and tooling.

---

## 🎯 Project Goals

The Patient Management System aims to:

- Provide a **clean, modular backend** for healthcare-related features (patients, billing, auth, etc.).
- Serve as a **playground for modern backend patterns**:
    - Domain-driven design & clear service boundaries.
    - Integration with **Docker**, **PostgreSQL**, **Kafka**, **gRPC**, and **AWS**.
    - **API Gateway**–driven access and security.
- Demonstrate **production-minded practices**:
    - Integration & API testing.
    - Observability and logging.
    - CI/CD-friendly structure and test deployment strategies.

---

## 🧱 Project Structure

This is a **Maven multi-module** project. At the moment, the structure is minimal and will expand over time:

```text
patient-management/
├── patient-service/        # Core service for patient CRUD operations
└── README.md               # This file