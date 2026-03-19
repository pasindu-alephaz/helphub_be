-- liquibase formatted sql

-- changeset antigravity:40
ALTER TABLE "users" ADD COLUMN IF NOT EXISTS "date_of_birth" DATE DEFAULT NULL;
