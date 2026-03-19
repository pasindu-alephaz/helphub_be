-- liquibase formatted sql

-- changeset antigravity:35
-- Add job_type and preferred_price columns to jobs table
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS job_type VARCHAR(20);
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS preferred_price DECIMAL(10,2);
