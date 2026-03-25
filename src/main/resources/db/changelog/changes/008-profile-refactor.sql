-- liquibase formatted sql

-- changeset antigravity:101
-- Rename first_name to full_name and migrate data
ALTER TABLE "users" RENAME COLUMN "first_name" TO "full_name";
UPDATE "users" SET "full_name" = TRIM(COALESCE("full_name", '') || ' ' || COALESCE("last_name", ''));

-- changeset antigravity:102
-- Add display_name and migrate bio to about, date_of_birth to birthday
ALTER TABLE "users" ADD COLUMN IF NOT EXISTS "display_name" VARCHAR(255);
ALTER TABLE "users" ADD COLUMN IF NOT EXISTS "about" TEXT;
UPDATE "users" SET "about" = "bio";
ALTER TABLE "users" ADD COLUMN IF NOT EXISTS "birthday" DATE;
UPDATE "users" SET "birthday" = "date_of_birth";

-- changeset antigravity:103
-- Drop old columns
ALTER TABLE "users" DROP COLUMN IF EXISTS "last_name";
ALTER TABLE "users" DROP COLUMN IF EXISTS "bio";
ALTER TABLE "users" DROP COLUMN IF EXISTS "date_of_birth";

-- changeset antigravity:104
-- Add tracking and file placeholders
ALTER TABLE "users" ADD COLUMN IF NOT EXISTS "last_verified_at" TIMESTAMP;
ALTER TABLE "users" ADD COLUMN IF NOT EXISTS "profile_picture_id" UUID;
ALTER TABLE "users" ADD COLUMN IF NOT EXISTS "identity_verification_id" UUID;

-- changeset antigravity:105
-- Add foreign keys to images for new profile files
ALTER TABLE "users" ADD CONSTRAINT fk_user_profile_picture FOREIGN KEY ("profile_picture_id") REFERENCES "images"("id") ON DELETE SET NULL;
ALTER TABLE "users" ADD CONSTRAINT fk_user_identity_verification FOREIGN KEY ("identity_verification_id") REFERENCES "images"("id") ON DELETE SET NULL;

-- changeset antigravity:106
-- Update user_addresses with country and geolocation
ALTER TABLE "user_addresses" ADD COLUMN IF NOT EXISTS "country" VARCHAR(100);
-- PostGIS removed as it is not available on the host machine.
-- We use existing latitude and longitude columns instead.

-- changeset antigravity:107
-- Create user_educations table
CREATE TABLE IF NOT EXISTS "user_educations" (
    "id" UUID DEFAULT gen_random_uuid(),
    "user_id" UUID NOT NULL,
    "educational_level" VARCHAR(100) NOT NULL,
    "certificates_id" UUID,
    "created_at" TIMESTAMP DEFAULT now(),
    "updated_at" TIMESTAMP DEFAULT now(),
    PRIMARY KEY ("id"),
    CONSTRAINT fk_education_user FOREIGN KEY ("user_id") REFERENCES "users"("id") ON DELETE CASCADE,
    CONSTRAINT fk_education_certificate FOREIGN KEY ("certificates_id") REFERENCES "images"("id") ON DELETE SET NULL
);

-- changeset antigravity:108
-- Create user_professional_details table
CREATE TABLE IF NOT EXISTS "user_professional_details" (
    "id" UUID DEFAULT gen_random_uuid(),
    "user_id" UUID NOT NULL UNIQUE,
    "skills" TEXT,
    "experience" TEXT,
    "created_at" TIMESTAMP DEFAULT now(),
    "updated_at" TIMESTAMP DEFAULT now(),
    PRIMARY KEY ("id"),
    CONSTRAINT fk_professional_user FOREIGN KEY ("user_id") REFERENCES "users"("id") ON DELETE CASCADE
);

-- changeset antigravity:109
-- Create join table for user professional categories and subcategories
CREATE TABLE IF NOT EXISTS "user_professional_categories" (
    "professional_detail_id" UUID NOT NULL,
    "category_id" UUID NOT NULL,
    PRIMARY KEY ("professional_detail_id", "category_id"),
    CONSTRAINT fk_upc_detail FOREIGN KEY ("professional_detail_id") REFERENCES "user_professional_details"("id") ON DELETE CASCADE,
    CONSTRAINT fk_upc_category FOREIGN KEY ("category_id") REFERENCES "service_categories"("id") ON DELETE CASCADE
);
