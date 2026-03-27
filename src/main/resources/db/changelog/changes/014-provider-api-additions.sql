-- liquibase formatted sql

-- changeset helphub:014-provider-api-additions-1
ALTER TABLE "users" ADD COLUMN IF NOT EXISTS "gender" VARCHAR(20);

-- changeset helphub:014-provider-api-additions-2
ALTER TABLE "provider_identity_documents" ADD COLUMN IF NOT EXISTS "issuing_country" VARCHAR(100);
ALTER TABLE "provider_identity_documents" ADD COLUMN IF NOT EXISTS "issuing_country_code" VARCHAR(10);

-- changeset helphub:014-provider-api-additions-4
ALTER TABLE "user_addresses" ADD COLUMN IF NOT EXISTS "street_address" TEXT;
ALTER TABLE "user_educations" ADD COLUMN IF NOT EXISTS "certificate_name" VARCHAR(255);
ALTER TABLE "user_educations" ADD COLUMN IF NOT EXISTS "university" VARCHAR(255);

-- changeset helphub:014-provider-api-additions-5
-- Ensure provider_profiles table exists (in case it was dropped or not created)
CREATE TABLE IF NOT EXISTS "provider_profiles" (
    "id" UUID DEFAULT gen_random_uuid(),
    "user_id" UUID NOT NULL UNIQUE,
    "business_name" VARCHAR(200),
    "verification_status" VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    "verified_at" TIMESTAMP,
    "notes" TEXT,
    "average_rating" DECIMAL(3, 2) DEFAULT 0,
    "review_count" INTEGER DEFAULT 0,
    "is_verified_badge" BOOLEAN DEFAULT FALSE,
    "is_available" BOOLEAN DEFAULT TRUE,
    "created_at" TIMESTAMP DEFAULT now(),
    "updated_at" TIMESTAMP DEFAULT now(),
    "deleted_at" TIMESTAMP DEFAULT NULL,
    PRIMARY KEY("id"),
    CONSTRAINT fk_provider_user FOREIGN KEY("user_id") REFERENCES "users"("id") ON DELETE CASCADE
);
