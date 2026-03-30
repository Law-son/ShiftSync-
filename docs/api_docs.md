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

### 2. Log In
**Endpoint:** `POST /api/auth/login`

Authenticates a user and issues a fresh set of JWT access and refresh tokens. For security against XSS, tokens are delivered exclusively embedded in `HttpOnly` Set-Cookie headers.

**Request Payload:**
```json
{
  "email": "jane.doe@example.com",
  "password": "StrongPassword123!"
}
```

**Responses:**
- `200 OK`: Successful authentication. Response includes user details. Cookies are set securely.
- `401 Unauthorized`: Distinguishes between "User not found" and "Incorrect password" for tailored feedback.

### 3. Refresh Token
**Endpoint:** `POST /api/auth/refresh`

Exchanges a valid refresh token for a brand new set of access and refresh tokens.

- The system prioritizes reading the `refreshToken` from the `HttpOnly` cookie.
- If no cookie is present, it falls back to inspecting the `Authorization: Bearer <refreshToken>` header.

**Responses:**
- `200 OK`: Tokens successfully refreshed. Updates the `HttpOnly` cookies.
- `401 Unauthorized`: Refresh token is missing, invalid, or expired.

## Role-Based Access Enforcement (RBAC) Test Endpoints

A temporary `TestRoleController` has been implemented to verify ShiftSync's method-level security (`@PreAuthorize()`) and unified global exception handling behavior.

### 1. Employee-Only Access
**Endpoint:** `GET /api/test-roles/employee`
- Valid Role: `EMPLOYEE`
- Responses: `200 OK` (success) | `401 Unauthorized` (missing/invalid token) | `403 Forbidden` (wrong role)

### 2. Manager-Only Access
**Endpoint:** `GET /api/test-roles/manager`
- Valid Role: `MANAGER`
- Responses: Same as above.

### 3. HR Admin-Only Access
**Endpoint:** `GET /api/test-roles/hr-admin`
- Valid Role: `HR_ADMIN`
- Responses: Same as above.

### 4. Manager OR HR Admin Access
**Endpoint:** `GET /api/test-roles/manager-or-hr`
- Valid Roles: `MANAGER` or `HR_ADMIN`
- Responses: Same as above.

### Global Structured Error Format
If you hit an unsecured boundary without an appropriate role, you will reliably receive the following ShiftSync-standard JSON envelope:

```json
{
  "timestamp": "2026-03-30T10:00:00.00000",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied: You do not have sufficient privileges to perform this action.",
  "fieldErrors": {}
}
```
