-- liquibase formatted sql

-- changeset antigravity:43
ALTER TABLE bids ADD COLUMN IF NOT EXISTS "job_availability_duration" VARCHAR(50);

-- changeset antigravity:44
ALTER TABLE messages ADD COLUMN IF NOT EXISTS "suggested_availability_duration" VARCHAR(50);