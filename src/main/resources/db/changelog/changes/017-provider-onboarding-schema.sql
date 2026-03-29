-- liquibase formatted sql

-- changeset helphub:017-provider-onboarding-fields
ALTER TABLE "provider_profiles" ADD COLUMN IF NOT EXISTS "terms_accepted" BOOLEAN DEFAULT FALSE;
ALTER TABLE "provider_profiles" ADD COLUMN IF NOT EXISTS "terms_accepted_at" TIMESTAMP;
ALTER TABLE "provider_profiles" ADD COLUMN IF NOT EXISTS "terms_version" VARCHAR(50);
ALTER TABLE "provider_profiles" ADD COLUMN IF NOT EXISTS "rejection_reason" TEXT;
ALTER TABLE "provider_profiles" ADD COLUMN IF NOT EXISTS "verified_at" TIMESTAMP;

-- changeset helphub:017-provider-skill-proofs-table
CREATE TABLE IF NOT EXISTS "provider_skill_proofs" (
    "id" UUID PRIMARY KEY,
    "provider_profile_id" UUID NOT NULL,
    "title" VARCHAR(200),
    "description" TEXT,
    "file_url" VARCHAR(255) NOT NULL,
    "file_type" VARCHAR(50),
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "fk_skill_proof_provider" FOREIGN KEY ("provider_profile_id") REFERENCES "provider_profiles"("id") ON DELETE CASCADE
);
