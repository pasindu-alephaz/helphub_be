# Frontend Authentication Integration Guide

HelpHub uses a **Phone-First** authentication strategy. This guide explains how to integrate the backend APIs to handle user login, registration, and social linking.

> [!IMPORTANT]
> All successful authentication responses now return **two tokens**: a short-lived `accessToken` (JWT, 15 min) and a long-lived `refreshToken` (opaque UUID, 7 days). Use the `accessToken` in the `Authorization: Bearer` header for API calls, and use `refreshToken` to silently renew it.

---

## Standard Response Envelope

All authentication responses return a standard envelope.

```json
{
  "status": true,
  "status_code": "SUCCESS",
  "data": {
    "accessToken": "SHORT_LIVED_JWT",
    "refreshToken": "LONG_LIVED_UUID",
    "phoneVerificationRequired": false,
    "registrationRequired": false,
    "pendingToken": "OPTIONAL_UUID"
  }
}
```

---

## 1. Phone Number Authentication Flow

### Step 1: Initiate OTP
`POST /api/v1/auth/phone/init` — sends OTP to the phone number.

```json
{ "phoneNumber": "+94771234567" }
```

### Step 2: Verify OTP
`POST /api/v1/auth/phone/verify`

```json
{ "phoneNumber": "+94771234567", "otp": "123456" }
```

**Next Step logic:**
1. **If `data.accessToken` is present**: Logged in. Save both tokens. Redirect to **Dashboard**.
2. **If `data.registrationRequired` is true**: New user. Redirect to **Registration Screen**. Note the `pendingToken`.

---

## 2. Social Login Flow (Google/Apple)

### Step 1: Verify Social Token
`POST /api/v1/auth/google` or `POST /api/v1/auth/apple`

```json
{ "token": "VENDOR_ID_TOKEN" }
```
Returns `phoneVerificationRequired: true` and a `pendingToken`.

### Step 2: Phone Verification (pass `pendingToken`)
1. `POST /api/v1/auth/phone/init`: `{ "phoneNumber": "...", "pendingToken": "FROM_STEP_1" }`
2. `POST /api/v1/auth/phone/verify`: `{ "phoneNumber": "...", "otp": "...", "pendingToken": "FROM_STEP_1" }`

---

## 3. Completing Registration

`POST /api/v1/auth/phone/complete-registration`

```json
{
  "pendingToken": "UUID_FROM_VERIFY_STEP",
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "1995-05-15",
  "email": "john@example.com"
}
```
Returns `accessToken` + `refreshToken`. Redirect to **Dashboard**.

---

## 4. Token Refresh

When the `accessToken` expires (15 min), call this endpoint silently:

`POST /api/v1/auth/refresh`

```json
{ "refreshToken": "YOUR_STORED_REFRESH_TOKEN" }
```

Returns a new `accessToken` (and the same `refreshToken`). If this fails with `401`, the refresh token has expired — redirect to login.

---

## 5. Logout

`POST /api/v1/auth/logout` *(requires `Authorization: Bearer <accessToken>`)*

```json
{ "refreshToken": "YOUR_STORED_REFRESH_TOKEN" }
```

Revokes the refresh token. After this, calling `/auth/refresh` with that token will return `401`.

---

## 6. Admin Authentication

`POST /api/v1/admin/auth/login` → `POST /api/v1/admin/auth/verify-2fa`

Admin tokens follow the **same** access + refresh pattern.

---

## Summary of Status Codes

| Code | Usage | Frontend Action |
|------|-------|-----------------|
| `SUCCESS` | Tokens returned | Save tokens, go to Dashboard |
| `PHONE_VERIFICATION_REQUIRED` | Social login step 1 done | Go to Phone Input screen |
| `REGISTRATION_REQUIRED` | New user verified phone | Go to Registration Form |
| `TWO_FA_REQUIRED` | Admin login step 1 done | Go to Email OTP screen |
| `VALIDATION_ERROR` | Request data invalid | Show field errors |
| `UNAUTHORIZED` | Invalid credentials/token | Show login error |

---

## 7. Handling Token Expiration

If an API request returns `401 Unauthorized`, it usually means the `accessToken` has expired. 

### Recommended Flow:
1. Intercept the `401` error in your API client (e.g., using Axios interceptors).
2. Call `POST /api/v1/auth/refresh` with your stored `refreshToken`.
3. If refresh succeeds:
   - Save the new `accessToken`.
   - Retry the original failed request with the new token.
4. If refresh fails (also returns `401`):
   - The user's session has fully expired.
   - Clear all stored tokens and redirect to the **Login** screen.
