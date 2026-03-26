-- liquibase formatted sql
-- changeset antigravity:63
-- Fix job_templates table
ALTER TABLE job_templates ADD COLUMN IF NOT EXISTS "job_availability_duration" VARCHAR(50);
ALTER TABLE job_templates ADD COLUMN IF NOT EXISTS "job_plan" VARCHAR(100);
ALTER TABLE job_templates ADD COLUMN IF NOT EXISTS "preferred_language" VARCHAR(50);
ALTER TABLE job_templates ADD COLUMN IF NOT EXISTS "job_type" VARCHAR(20);
ALTER TABLE job_templates ADD COLUMN IF NOT EXISTS "urgency_flag" VARCHAR(20);
ALTER TABLE job_templates ADD COLUMN IF NOT EXISTS "preferred_price" DECIMAL(10,2);

-- Fix jobs table
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS "job_availability_duration" VARCHAR(50);
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS "job_plan" VARCHAR(100);
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS "preferred_language" VARCHAR(50);
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS "job_type" VARCHAR(20);
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS "urgency_flag" VARCHAR(20);
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS "preferred_price" DECIMAL(10,2);
