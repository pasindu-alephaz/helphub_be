-- liquibase formatted sql

-- changeset helphub:016-provider-profile-bio
ALTER TABLE "provider_profiles" ADD COLUMN IF NOT EXISTS "bio" TEXT;
