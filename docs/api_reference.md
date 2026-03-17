# HelpHub API Reference

Base URL: `http://localhost:8080/api/v1`

All responses wrap data in a standard envelope:

```json
{
  "status": true,
  "status_code": "SUCCESS",
  "message": "...",
  "data": { }
}
```

> **Authentication:** Endpoints marked with 🔒 require a valid JWT token in the `Authorization: Bearer <token>` header. Endpoints marked with 🔑 additionally require specific role permissions.

---

## 1. Authentication

Base path: `/api/v1/auth`

| Method | Endpoint | Auth | Summary |
|--------|----------|------|---------|
| `POST` | `/register` | Public | Register a new user |
| `POST` | `/login` | Public | Log in and get a JWT token |
| `POST` | `/verify-2fa` | Public | Verify 2FA OTP to complete login |
| `POST` | `/forgot-password` | Public | Send password-reset OTP to email |
| `POST` | `/reset-password` | Public | Reset password using OTP |

---

### POST `/register`

Registers a new user.

**Request Body**
```json
{
  "name": "string",
  "email": "string",
  "password": "string"
}
```

**Responses**

| Code | Description |
|------|-------------|
| `200` | User registered successfully |
| `400` | Validation error in request body |
| `409` | Email already exists |

---

### POST `/login`

Authenticates a user and returns a JWT token. If 2FA is enabled, the token is withheld until OTP verification.

**Request Body**
```json
{
  "email": "string",
  "password": "string"
}
```

**Responses**

| Code | Description |
|------|-------------|
| `200` | Login successful. `data.token` contains the JWT. If `data.twoFactorRequired` is `true`, an OTP was sent to email. |
| `400` | Invalid request body |
| `401` | Invalid credentials |

---

### POST `/verify-2fa`

Completes the 2FA flow. Returns a JWT on success.

**Request Body**
```json
{
  "token": "string",
  "otp": "string"
}
```

**Responses**

| Code | Description |
|------|-------------|
| `200` | 2FA verified — `data.token` contains the JWT |
| `400` | Invalid or expired OTP |

---

### POST `/forgot-password`

Sends a password-reset OTP to the user's email.

**Request Body**
```json
{
  "email": "string"
}
```

**Responses**

| Code | Description |
|------|-------------|
| `200` | OTP sent successfully |
| `400` | User not found or invalid request |

---

### POST `/reset-password`

Resets the password using the OTP received by email.

**Request Body**
```json
{
  "email": "string",
  "otp": "string",
  "newPassword": "string"
}
```

**Responses**

| Code | Description |
|------|-------------|
| `200` | Password reset successfully |
| `400` | Invalid/expired OTP or invalid new password |

---

## 2. Verification

Base path: `/api/v1/auth/verification`

| Method | Endpoint | Auth | Summary |
|--------|----------|------|---------|
| `POST` | `/send` | Public | Send a verification OTP to email/phone |
| `POST` | `/verify` | Public | Verify the OTP using the returned token |

---

### POST `/send`

Sends a verification OTP to an email and/or phone number. Returns a token to use during verification.

**Request Body**
```json
{
  "email": "string (optional)",
  "phoneNumber": "string (optional)"
}
```
> At least one of `email` or `phoneNumber` is required.

**Responses**

| Code | Description |
|------|-------------|
| `200` | OTP sent. `data` contains token(s) to use in `/verify`. |
| `400` | Validation error |

---

### POST `/verify`

Verifies an OTP code using the token from the send step.

**Request Body**
```json
{
  "token": "string",
  "otp": "string"
}
```

**Responses**

| Code | Description |
|------|-------------|
| `200` | Verification successful |
| `400` | Invalid, expired, or already-used OTP |

---

## 3. Profile

Base path: `/api/v1/profile`

| Method | Endpoint | Auth | Required Permission |
|--------|----------|------|---------------------|
| `GET` | `/` | 🔒 JWT | `profile_read` |
| `PUT` | `/` | 🔒 JWT | `profile_update` |

---

### GET `/`

Retrieves the profile of the currently authenticated user.

**Responses**

| Code | Description |
|------|-------------|
| `200` | Profile retrieved successfully |
| `401` | Unauthorized |
| `404` | Profile not found |

---

### PUT `/`

Updates the profile of the currently authenticated user.

**Request Body**
```json
{
  "name": "string",
  "phoneNumber": "string"
}
```

**Responses**

| Code | Description |
|------|-------------|
| `200` | Profile updated successfully |
| `400` | Validation error |
| `401` | Unauthorized |
| `404` | Profile not found |

---

## 4. Service Categories

| Method | Endpoint | Auth | Summary |
|--------|----------|------|---------|
| `GET` | `/api/v1/categories` | Public | List all categories (flat or hierarchical) |
| `POST` | `/api/v1/categories` | 🔒 JWT | Create a top-level category |
| `GET` | `/api/v1/categories/{id}` | Public | Get a category by ID |
| `PUT` | `/api/v1/categories/{id}` | 🔒 JWT | Update a category |
| `DELETE` | `/api/v1/categories/{id}` | 🔒 JWT | Soft-delete a category |
| `POST` | `/api/v1/subcategories` | 🔒 JWT | Create a subcategory |
| `GET` | `/api/v1/subcategories/{id}` | Public | Get a subcategory by ID |
| `PUT` | `/api/v1/subcategories/{id}` | 🔒 JWT | Update a subcategory |
| `DELETE` | `/api/v1/subcategories/{id}` | 🔒 JWT | Soft-delete a subcategory |

---

### GET `/api/v1/categories`

Returns all categories. Use `?hierarchical=true` (default) for a nested tree structure or `?hierarchical=false` for a flat list.

**Query Parameters**

| Param | Type | Default | Description |
|-------|------|---------|-------------|
| `hierarchical` | boolean | `true` | Return nested or flat list |

**Responses:** `200 OK`

---

### POST `/api/v1/categories`

Creates a new top-level category.

**Request Body**
```json
{
  "name": "string",
  "description": "string"
}
```

**Responses:** `200 OK` / `400 Bad Request`

---

### GET `/api/v1/categories/{id}`

Retrieves a single category by its UUID.

**Path Variables:** `id` — UUID of the category

**Responses:** `200 OK` / `404 Not Found`

---

### PUT `/api/v1/categories/{id}`

Updates an existing top-level category.

**Request Body**
```json
{
  "name": "string",
  "description": "string"
}
```

**Responses:** `200 OK` / `404 Not Found`

---

### DELETE `/api/v1/categories/{id}`

Soft-deletes a top-level category.

**Responses:** `200 OK` / `404 Not Found`

---

### POST `/api/v1/subcategories`

Creates a new subcategory under a parent category.

**Request Body**
```json
{
  "name": "string",
  "description": "string",
  "parentId": "UUID"
}
```

**Responses:** `200 OK` / `400 Bad Request` (parent not found)

---

### GET `/api/v1/subcategories/{id}`

Retrieves a single subcategory by its UUID.

**Responses:** `200 OK` / `404 Not Found`

---

### PUT `/api/v1/subcategories/{id}`

Updates an existing subcategory.

**Request Body**
```json
{
  "name": "string",
  "description": "string"
}
```

**Responses:** `200 OK` / `404 Not Found`

---

### DELETE `/api/v1/subcategories/{id}`

Soft-deletes a subcategory.

**Responses:** `200 OK` / `404 Not Found`

---

## 5. Roles

Base path: `/api/v1/roles`

| Method | Endpoint | Auth | Required Permission |
|--------|----------|------|---------------------|
| `POST` | `/` | 🔑 | `role_create` |
| `GET` | `/` | 🔑 | `role_read` |
| `GET` | `/{id}` | 🔑 | `role_read` |
| `PUT` | `/{id}` | 🔑 | `role_update` |
| `DELETE` | `/{id}` | 🔑 | `role_delete` |
| `POST` | `/{id}/permissions` | 🔑 | `role_assign_permissions` |

---

### POST `/`

Creates a new role.

**Request Body**
```json
{
  "name": "string"
}
```

**Responses:** `201 Created` / `400 Bad Request`

---

### GET `/`

Returns all roles.

**Responses:** `200 OK`

---

### GET `/{id}`

Returns a role by its integer ID.

**Responses:** `200 OK` / `404 Not Found`

---

### PUT `/{id}`

Updates a role by its integer ID.

**Request Body**
```json
{
  "name": "string"
}
```

**Responses:** `200 OK` / `400 Bad Request` / `404 Not Found`

---

### DELETE `/{id}`

Deletes a role by its integer ID.

**Responses:** `204 No Content` / `404 Not Found`

---

### POST `/{id}/permissions`

Replaces the full set of permissions for a role.

**Request Body** — array of permission IDs:
```json
[1, 2, 3]
```

**Responses:** `200 OK` / `400 Bad Request` / `404 Not Found`

---

## 6. Permissions

Base path: `/api/v1/permissions`

| Method | Endpoint | Auth | Required Permission |
|--------|----------|------|---------------------|
| `GET` | `/` | 🔑 | `permission_read` |
| `GET` | `/{id}` | 🔑 | `permission_read` |

> Permissions are **read-only** — they are seeded by the system and cannot be created or deleted via the API.

---

### GET `/`

Returns all permissions.

**Responses:** `200 OK`

---

### GET `/{id}`

Returns a single permission by its integer ID.

**Responses:** `200 OK` / `404 Not Found`

---

## Standard Error Response

All error responses follow this format:

```json
{
  "status": false,
  "status_code": "NOT_FOUND",
  "message": "Resource not found",
  "errors": { }
}
```

Common status codes:

| Code | Meaning |
|------|---------|
| `SUCCESS` | Request completed successfully |
| `BAD_REQUEST` | Invalid input or business rule violation |
| `VALIDATION_ERROR` | Request body failed validation |
| `UNAUTHORIZED` | Missing or invalid JWT token |
| `FORBIDDEN` | JWT valid but insufficient permissions |
| `NOT_FOUND` | Resource not found |
| `TWO_FA_REQUIRED` | Login successful but 2FA OTP required |
