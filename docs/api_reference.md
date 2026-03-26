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

> **Authentication:** Endpoints marked with 🔒 require a valid access token (JWT) in the `Authorization: Bearer <accessToken>` header. Endpoints marked with 🔑 additionally require specific role permissions.

---

## 1. Authentication

Base path: `/api/v1/auth`

| Method | Endpoint | Auth | Summary |
|--------|----------|------|---------|
| `POST` | `/phone/init` | Public | Send OTP to phone number |
| `POST` | `/phone/verify` | Public | Verify phone OTP and get access/refresh tokens |
| `POST` | `/phone/complete-registration` | Public | Create user and get tokens |
| `POST` | `/google` | Public | Login with Google |
| `POST` | `/apple` | Public | Login with Apple |
| `POST` | `/refresh` | Public | Refresh expired access token |
| `POST` | `/logout` | 🔒 | Logout by revoking refresh token |
| `POST` | `/forgot-password` | Public | Initiate password reset via email |
| `POST` | `/reset-password` | Public | Complete password reset with OTP |

---

### POST `/phone/init`

Sends a 6-digit verification code to the provided phone number.

**Request Body**
```json
{
  "phoneNumber": "+94771234567",
  "pendingToken": "string (optional)"
}
```

**Parameters**
- `phoneNumber`: The user's mobile number in E.164 format.
- `pendingToken`: (Optional) The UUID returned by the `/google` or `/apple` endpoints. Provide this to link the phone verification to a social login identity. If omitted, the request is treated as a standard phone-only login/register.

**Responses**
- `200`: OTP sent successfully.

---

### POST `/phone/verify`

Verifies the OTP code. Returns `accessToken` and `refreshToken` if the user exists, otherwise returns a registration token.

**Request Body**
```json
{
  "phoneNumber": "+94771234567",
  "otp": "123456",
  "pendingToken": "string (optional)"
}
```

**Responses**
- `200`: Verification successful. Returns `data.accessToken` and `data.refreshToken` if user exists.
- `200`: Registration required. Returns `data.registrationRequired: true` and `data.pendingToken`.
- `400`: Invalid or expired OTP.

---

### POST `/phone/complete-registration`

Creates a new user profile.

**Request Body**
```json
{
  "pendingToken": "UUID_FROM_VERIFY_STEP",
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "1995-05-15",
  "email": "john@example.com (optional)"
}
```

---

### POST `/google` / `/apple`

Authenticates with a social provider token. Returns `phoneVerificationRequired: true` and a `pendingToken`.

---

### POST `/refresh`

Returns a new, short-lived `accessToken` given a valid `refreshToken`.

**Request Body**
```json
{
  "refreshToken": "YOUR_STORED_REFRESH_TOKEN"
}
```

**Responses**
- `200`: Success. Returns new `accessToken` and same `refreshToken`.
- `401`: Unauthorized. Refresh token expired or revoked.

---

### POST `/logout`

🔒 Revokes the provided `refreshToken`, effectively logging the user out from that specific session/device.

**Request Body**
```json
{
  "refreshToken": "YOUR_STORED_REFRESH_TOKEN"
}
```

**Responses**
- `200`: Successfully logged out.

---

## 2. Admin Authentication

Base path: `/api/v1/admin/auth`

| Method | Endpoint | Auth | Summary |
|--------|----------|------|---------|
| `POST` | `/login` | Public | Admin login with email/password |
| `POST` | `/verify-2fa` | Public | Verify admin email 2FA |

---

### POST `/admin/auth/login`

Authenticates an admin. Returns `TWO_FA_REQUIRED` if credentials are valid.

---

### POST `/admin/auth/verify-2fa`

Verifies the 2FA code sent to the admin's email. Returns `accessToken` and `refreshToken` on success.

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
| `GET` | `/addresses` | 🔒 JWT | List user addresses |
| `POST` | `/addresses` | 🔒 JWT | Add a new address |
| `PUT` | `/addresses/{id}` | 🔒 JWT | Update an address |
| `DELETE` | `/addresses/{id}` | 🔒 JWT | Delete an address |
| `PUT` | `/addresses/{id}/default` | 🔒 JWT | Set as default address |

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

### 4.1 User Facing APIs
| Method | Endpoint | Auth | Summary |
|--------|----------|------|---------|
| `GET` | `/api/v1/categories` | Public | List all categories (flat or hierarchical) |
| `GET` | `/api/v1/categories/{id}` | Public | Get a category by ID |
| `GET` | `/api/v1/categories/{categoryId}/subcategories/{subCategoryId}` | Public | Get a subcategory by ID |
| `POST` | `/api/v1/categories/request` | 🔒 JWT | Request/suggest a new category |
| `POST` | `/api/v1/categories/{categoryId}/subcategories/request` | 🔒 JWT | Request/suggest a new subcategory |

### 4.2 Admin Management APIs 🔑
| Method | Endpoint | Summary |
|--------|----------|---------|
| `GET` | `/api/v1/admin/categories` | List all categories with full details |
| `GET` | `/api/v1/admin/subcategories` | List all subcategories |
| `POST` | `/api/v1/admin/categories` | Create a top-level category |
| `GET` | `/api/v1/admin/categories/{id}` | Get a category by ID |
| `PUT` | `/api/v1/admin/categories/{id}` | Update a category |
| `DELETE` | `/api/v1/admin/categories/{id}` | Soft-delete a category |
| `POST` | `/api/v1/admin/categories/{categoryId}/subcategories` | Create a subcategory |
| `GET` | `/api/v1/admin/categories/{categoryId}/subcategories/{subCategoryId}` | Get a subcategory by ID |
| `PUT` | `/api/v1/admin/categories/{categoryId}/subcategories/{subCategoryId}` | Update a subcategory |
| `DELETE` | `/api/v1/admin/categories/{categoryId}/subcategories/{subCategoryId}` | Soft-delete a subcategory |
| `GET` | `/api/v1/admin/categories/requests` | List pending category requests |
| `POST` | `/api/v1/admin/categories/requests/{id}/approve` | Approve a request |
| `POST` | `/api/v1/admin/categories/requests/{id}/reject` | Reject a request |

---

### GET `/api/v1/categories`

Returns all categories. Use `?hierarchical=true` (default) for a nested tree structure or `?hierarchical=false` for a flat list.

**Query Parameters**

| Param | Type | Default | Description |
|-------|------|---------|-------------|
| `hierarchical` | boolean | `true` | Return nested or flat list |

**Responses:** `200 OK`

---

### POST `/api/v1/admin/categories`

Creates a new top-level category. Uses `multipart/form-data`.

**Request Parts**
- `category`: JSON object containing:
  - `name`: Map<String, String> (localized)
  - `description`: Map<String, String> (localized, optional)
  - `displayOrder`: Integer
- `image`: File (optional, category icon)

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

### POST `/api/v1/admin/categories/{categoryId}/subcategories`

Creates a new subcategory. Uses `multipart/form-data`.

**Request Parts**
- `subcategory`: JSON object containing:
  - `name`: Map<String, String>
  - `description`: Map<String, String>
  - `displayOrder`: Integer
- `image`: File (optional, icon)

**Responses:** `200 OK` / `400 Bad Request`

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

---

## 7. Jobs 🔒

| Method | Endpoint | Summary |
|--------|----------|---------|
| `POST` | `/api/v1/jobs` | Create a new job request |
| `GET` | `/api/v1/jobs` | List and filter jobs (Public/Authenticated) |
| `GET` | `/api/v1/jobs/{id}` | Get job details |
| `PUT` | `/api/v1/jobs/{id}` | Update job details |
| `DELETE` | `/api/v1/jobs/{id}` | Soft-delete/cancel a job |
| `POST` | `/api/v1/jobs/{id}/images` | Upload job images (multipart) |
| `GET` | `/api/v1/jobs/{id}/images` | List job images |
| `POST` | `/api/v1/jobs/{id}/accept` | Accept a job (Provider) |
| `POST` | `/api/v1/jobs/{id}/start` | Mark job as started |
| `POST` | `/api/v1/jobs/{id}/complete` | Confirm completion (Poster) |
| `GET` | `/api/v1/jobs/nearby` | Find jobs by coordinates |
| `GET` | `/api/v1/jobs/my-jobs` | List user's posted jobs |
| `GET` | `/api/v1/jobs/accepted` | List provider's accepted jobs |

---

## 8. Job Bidding (Bids) 🔒 

Base path: `/api/v1/jobs/{id}/bids`

| Method | Endpoint | Summary |
|--------|----------|---------|
| `POST` | `/` | Submit a bid (Provider) |
| `GET` | `/` | View all bids for a job |
| `PUT` | `/{bidId}` | Adjust a submitted bid |
| `POST` | `/{bidId}/accept` | Accept a specific bid |

---

## 9. Job Negotiation (Messages) 🔒

Base path: `/api/v1/jobs/{id}/messages`

| Method | Endpoint | Summary |
|--------|----------|---------|
| `POST` | `/` | Send a negotiation message/chat |
| `GET` | `/` | Get chat history for a job |
| `POST` | `/{messageId}/accept` | Accept a price/schedule suggestion |

---

## 10. Provider Onboarding & Profiles 🔒

Base path: `/api/v1/providers`

| Method | Endpoint | Summary |
|--------|----------|---------|
| `POST` | `/onboarding/identity` | Step 1: Identity verification (multipart) |
| `POST` | `/onboarding/certificates` | Step 2: Add professional certificates |
| `POST` | `/onboarding/services` | Step 3: Select service categories |
| `POST` | `/onboarding/availability` | Step 4: Set availability schedule |
| `POST` | `/onboarding/portfolio` | Add portfolio items |
| `GET` | `/onboarding/me` | Get current provider onboarding status |
| `GET` | `/{id}/profile` | Retrieve public provider profile |

---

## 11. Notifications 🔒

Base path: `/api/v1/notifications`

| Method | Endpoint | Summary |
|--------|----------|---------|
| `GET` | `/` | List user notifications (Paginated) |
| `PUT` | `/mark-as-read/{id}` | Mark a specific notification as read |
| `PUT` | `/mark-as-read` | Mark all notifications as read |
| `DELETE` | `/{id}` | Soft delete a notification |
| `GET` | `/stream` | Subscribe to real-time updates (SSE) |

---

## 12. Admin - User Management 🔑

Base path: `/api/v1/admin/users`

| Method | Endpoint | Summary |
|--------|----------|---------|
| `GET` | `/` | List all users (Paginated & Filterable) |
| `GET` | `/{id}` | Get user by ID |
| `POST` | `/` | Create a new user account |
| `PUT` | `/{id}` | Update user account |
| `DELETE` | `/{id}` | Soft delete a user |
| `GET` | `/statistics` | Get platform user statistics |

---

## 13. Admin - Provider Verification 🔑

Base path: `/api/v1/admin`

| Method | Endpoint | Summary |
|--------|----------|---------|
| `PATCH` | `/providers/{id}/verify` | Approve or reject a provider profile |
| `PATCH` | `/certificates/{id}/verify` | Verify a provider certificate |

---

---

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

---

## Configuration

Some backend behaviors can be controlled via `application.properties` or environment variables:

| Property | Default | Description |
|----------|---------|-------------|
| `textit.sms.enabled` | `false` | If `true`, actual SMS messages are sent via Textit.biz. If `false`, messages are only logged to the console. |
| `textit.api-key` | `YOUR_API_KEY_HERE` | Basic Auth key for Textit.biz API. |
| `app.rate-limit.otp-send.requests-per-minute` | `5` | Maximum OTP initiation requests per minute per phone number. |
