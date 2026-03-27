-- liquibase formatted sql

-- changeset helphub:015-provider-skills-relationship
ALTER TABLE "provider_services" ADD COLUMN IF NOT EXISTS "relationship" VARCHAR(100);
