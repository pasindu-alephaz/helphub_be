# Frontend Authentication Integration Guide

HelpHub uses a **Phone-First** authentication strategy. This guide explains how to integrate the backend APIs to handle user login, registration, and social linking.

---

## Standard Response Envelope

All authentication responses return a standard envelope. Key fields for navigation are `status_code`, `data.phoneVerificationRequired`, `data.registrationRequired`, and `data.pendingToken`.

```json
{
  "status": true,
  "status_code": "SUCCESS",
  "data": {
    "token": "JWT_IF_SUCCESSFUL",
    "phoneVerificationRequired": false,
    "registrationRequired": false,
    "pendingToken": "OPTIONAL_UUID"
  }
}
```

---

## 1. Phone Number Authentication Flow

This is the primary flow for both new and existing users.

### Step 1: Initiate OTP
Call `POST /api/v1/auth/phone/init` when the user enters their phone number.

**Request:**
```json
{
  "phoneNumber": "+94771234567"
}
```

**Response:**
Returns `status_code: "SUCCESS"`. The backend logs the OTP in the console in [DEV] mode and sends an SMS via Textit.

### Step 2: Verify OTP
Call `POST /api/v1/auth/phone/verify` when the user enters the 6-digit code.

**Request:**
```json
{
  "phoneNumber": "+94771234567",
  "otp": "123456"
}
```

**Next Step logic:**
1.  **If `data.token` is present**: The user is logged in. Redirect to **Dashboard**.
2.  **If `data.registrationRequired` is true**: The user is new. Redirect to **Registration Screen**. Note the `pendingToken` for the next step.
    *   *Status Code:* `REGISTRATION_REQUIRED`

---

## 2. Social Login Flow (Google/Apple)

Social logins are multi-step: Identity Verification -> Phone Verification -> (Optional) Registration.

### Step 1: Verify Social Token
Call `POST /api/v1/auth/google` or `POST /api/v1/auth/apple` with the token received from the vendor.

**Request:**
```json
{
  "token": "VENDOR_ID_TOKEN"
}
```

**Response:**
The backend will ALWAYS return `phoneVerificationRequired: true` and a `pendingToken`.
*   *Status Code:* `PHONE_VERIFICATION_REQUIRED`

### Step 2: Phone Verification (Social Session)
Redirect the user to the Phone Input screen. When they initiate and verify the phone number, you **MUST** include the `pendingToken` from Step 1.

1.  `POST /api/v1/auth/phone/init`: `{ "phoneNumber": "...", "pendingToken": "FROM_STEP_1" }`
2.  `POST /api/v1/auth/phone/verify`: `{ "phoneNumber": "...", "otp": "...", "pendingToken": "FROM_STEP_1" }`

**Next Step logic:**
*   If user already exists (linked by email or phone): Returns `data.token`. Redirect to **Dashboard**.
*   If user is new: Returns `registrationRequired: true`. Redirect to **Registration Screen**.

---

## 3. Completing Registration

If the previous steps resulted in `registrationRequired: true`, show a form to collect user details.

`POST /api/v1/auth/phone/complete-registration`

**Request:**
```json
{
  "pendingToken": "UUID_FROM_VERIFY_STEP",
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "1995-05-15",
  "email": "john@example.com" (optional)
}
```

**Response:**
Returns `data.token` (JWT). Redirect to **Dashboard**.

---

## 4. Admin Authentication

Admins use a traditional Email/Password flow with mandatory 2FA.

### Step 1: Login
`POST /api/v1/admin/auth/login`
*   Returns `TWO_FA_REQUIRED` status and `twoFactorRequired: true` if credentials are valid.
*   An OTP is sent to the admin's email.

### Step 2: Verify
`POST /api/v1/admin/auth/verify-2fa`
*   Requires `email` and `otp`.
*   Returns JWT on success.

---

## Summary of Status Codes

| Code | Usage | Frontend Action |
|------|-------|-----------------|
| `SUCCESS` | JWT returned | Save token, go to Dashboard |
| `PHONE_VERIFICATION_REQUIRED` | Social login step 1 done | Go to Phone Input screen |
| `REGISTRATION_REQUIRED` | New user verified phone | Go to Registration Form |
| `TWO_FA_REQUIRED` | Admin login step 1 done | Go to Email OTP screen |
| `VALIDATION_ERROR` | Request data invalid | Show field errors |
| `UNAUTHORIZED` | Invalid credentials/token | Show login error |
