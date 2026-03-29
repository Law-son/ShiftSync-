# ShiftSync API Documentation

Welcome to the ShiftSync API documentation. This API powers the workforce scheduling platform for Horizon Hospitality Group.

## Accessing Swagger UI

The interactive Swagger UI provides detailed endpoint exploration capabilities and allows you to easily test requests.

- **Local Development Environment URL:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **JSON OpenAPI definitions (v3):** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

> [!NOTE]
> The Swagger UI paths are default for `springdoc-openapi` in Spring Boot 3+. Ensure the application is running at port `8080` (or the configured application port).

## Overview
This document will comprehensively maintain records of the API documentation logic, specific design decisions, authorization procedures, and sample payloads. As we build out ShiftSync, this file will act as a centralized index.

For now, refer to the Swagger UI linked above for an actionable and up-to-date representation of all exposed API endpoints.

## Version
- **API Version:** 1.0.0
- **OpenAPI Specification:** 3.0

## Authentication

### 1. Register a New User
**Endpoint:** `POST /api/auth/register`

Creates a new user account with employee details and sets up their initial system role.

**Request Payload:**
```json
{
  "name": "Jane Doe",
  "email": "jane.doe@example.com",
  "password": "StrongPassword123!",
  "role": "EMPLOYEE"
}
```

*Note: Passwords must be at least 8 characters and contain an uppercase letter, lowercase letter, number, and special character.*

**Responses:**
- `201 Created`: User successfully registered.
- `400 Bad Request`: Validation errors (e.g., missing fields, weak password, or email already exists).

*(Other authentication endpoints such as login/token refresh will be populated as they are built)*
