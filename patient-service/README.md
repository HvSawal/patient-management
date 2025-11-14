# 🩺 Patient Service

The **Patient Service** is a Spring Boot–based REST API that manages **patient records** as part of the broader **Patient Management System**.  
It currently exposes **CRUD operations** for patient entities and acts as the foundational service other modules (e.g., billing, auth, gateway) will integrate with.

---

## ✨ Features

- Create new patients
- Retrieve a single patient by ID
- List all patients
- Update existing patients
- Delete patients by ID

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

> ℹ️ The table and some seed data can be initialized via `schema.sql` / `data.sql` when running locally.

---

## 🔗 REST API Overview

> **Base URL:** `http://localhost:8080/api/patients`

| Method   | Endpoint                  | Description                    |
|----------|---------------------------|--------------------------------|
| `POST`   | `/api/patients`           | Create a new patient           |
| `GET`    | `/api/patients/{id}`      | Get a patient by ID            |
| `GET`    | `/api/patients`           | List all patients              |
| `PUT`    | `/api/patients/{id}`      | Update an existing patient     |
| `DELETE` | `/api/patients/{id}`      | Delete a patient by ID         |

---

## 🧪 REST API Examples

### Example 1 : Create Patient

```bash
curl -X POST http://localhost:8080/api/patients \
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
curl -X GET http://localhost:8080/api/patients
```

### Example 3 : Get Patient by ID

```bash
curl -X GET http://localhost:8080/api/patients/123e4567-e89b-12d3-a456-426614174000
```

### Example 4 : Update Patient

```bash
curl -X PUT http://localhost:8080/api/patients/123e4567-e89b-12d3-a456-426614174000 \
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
curl -X DELETE \
  http://localhost:8080/api/patients/123e4567-e89b-12d3-a456-426614174000
```
**NOTE: Replace the above ID numbers with your specific UUID