-- liquibase formatted sql

-- changeset antigravity:37
-- Add missing job fields for availability duration, plan, and preferred language
ALTER TABLE "jobs" ADD COLUMN IF NOT EXISTS "job_availability_duration" VARCHAR(50);
ALTER TABLE "jobs" ADD COLUMN IF NOT EXISTS "job_plan" VARCHAR(100);
ALTER TABLE "jobs" ADD COLUMN IF NOT EXISTS "preferred_language" VARCHAR(50);

-- changeset antigravity:38
-- Add the same fields to job_templates table for consistency
ALTER TABLE "job_templates" ADD COLUMN IF NOT EXISTS "job_availability_duration" VARCHAR(50);
ALTER TABLE "job_templates" ADD COLUMN IF NOT EXISTS "job_plan" VARCHAR(100);
ALTER TABLE "job_templates" ADD COLUMN IF NOT EXISTS "preferred_language" VARCHAR(50);
