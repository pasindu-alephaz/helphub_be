-- Liquibase Migration: 004-phone-otp-auth
-- Adds phone_otps table and makes users.email nullable

CREATE TABLE phone_otps (
    id UUID PRIMARY KEY,
    phone_number VARCHAR(20) NOT NULL,
    otp VARCHAR(6) NOT NULL,
    purpose VARCHAR(20) NOT NULL,
    pending_token VARCHAR(255),
    expires_at TIMESTAMP NOT NULL,
    used_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Make email nullable on users table
ALTER TABLE users ALTER COLUMN email DROP NOT NULL;
