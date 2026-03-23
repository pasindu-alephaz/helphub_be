-- liquibase formatted sql

-- changeset antigravity:36
-- Add job_type and preferred_price columns to job_templates table
ALTER TABLE job_templates ADD COLUMN IF NOT EXISTS job_type VARCHAR(20);
ALTER TABLE job_templates ADD COLUMN IF NOT EXISTS preferred_price DECIMAL(10,2);
