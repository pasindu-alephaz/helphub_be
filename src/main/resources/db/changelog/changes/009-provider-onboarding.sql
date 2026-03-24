-- liquibase formatted sql

-- changeset helphub:009-provider-onboarding-1
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
    "created_at" TIMESTAMP DEFAULT now(),
    "updated_at" TIMESTAMP DEFAULT now(),
    "deleted_at" TIMESTAMP DEFAULT NULL,
    PRIMARY KEY("id"),
    CONSTRAINT fk_provider_user FOREIGN KEY("user_id") REFERENCES "users"("id") ON DELETE CASCADE
);

-- changeset helphub:009-provider-onboarding-2
CREATE TABLE IF NOT EXISTS "provider_identity_documents" (
    "id" UUID DEFAULT gen_random_uuid(),
    "provider_profile_id" UUID NOT NULL,
    "id_type" VARCHAR(20) NOT NULL, -- NIC, PASSPORT, LICENSE
    "id_number" VARCHAR(100) NOT NULL,
    "front_image_url" TEXT,
    "back_image_url" TEXT,
    "selfie_image_url" TEXT,
    "status" VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    "created_at" TIMESTAMP DEFAULT now(),
    "updated_at" TIMESTAMP DEFAULT now(),
    PRIMARY KEY("id"),
    CONSTRAINT fk_iden_provider FOREIGN KEY("provider_profile_id") REFERENCES "provider_profiles"("id") ON DELETE CASCADE
);

-- changeset helphub:009-provider-onboarding-3
CREATE TABLE IF NOT EXISTS "provider_certificates" (
    "id" UUID DEFAULT gen_random_uuid(),
    "provider_profile_id" UUID NOT NULL,
    "name" VARCHAR(200) NOT NULL,
    "file_url" TEXT NOT NULL,
    "issued_date" DATE,
    "verified_at" TIMESTAMP,
    "created_at" TIMESTAMP DEFAULT now(),
    "updated_at" TIMESTAMP DEFAULT now(),
    PRIMARY KEY("id"),
    CONSTRAINT fk_cert_provider FOREIGN KEY("provider_profile_id") REFERENCES "provider_profiles"("id") ON DELETE CASCADE
);

-- changeset helphub:009-provider-onboarding-4
CREATE TABLE IF NOT EXISTS "provider_services" (
    "id" UUID DEFAULT gen_random_uuid(),
    "provider_profile_id" UUID NOT NULL,
    "category_id" UUID NOT NULL,
    "skill_level" VARCHAR(20) NOT NULL DEFAULT 'BEGINNER', -- BEGINNER, INTERMEDIATE, EXPERT
    "created_at" TIMESTAMP DEFAULT now(),
    "updated_at" TIMESTAMP DEFAULT now(),
    PRIMARY KEY("id"),
    CONSTRAINT fk_ps_provider FOREIGN KEY("provider_profile_id") REFERENCES "provider_profiles"("id") ON DELETE CASCADE,
    CONSTRAINT fk_ps_category FOREIGN KEY("category_id") REFERENCES "service_categories"("id") ON DELETE CASCADE,
    UNIQUE("provider_profile_id", "category_id")
);

-- changeset helphub:009-provider-onboarding-5
CREATE TABLE IF NOT EXISTS "provider_availability" (
    "id" UUID DEFAULT gen_random_uuid(),
    "provider_profile_id" UUID NOT NULL,
    "day_of_week" INTEGER NOT NULL, -- 1 (Monday) to 7 (Sunday)
    "start_time" TIME NOT NULL,
    "end_time" TIME NOT NULL,
    "created_at" TIMESTAMP DEFAULT now(),
    "updated_at" TIMESTAMP DEFAULT now(),
    PRIMARY KEY("id"),
    CONSTRAINT fk_avail_provider FOREIGN KEY("provider_profile_id") REFERENCES "provider_profiles"("id") ON DELETE CASCADE
);

-- changeset helphub:009-provider-onboarding-6
CREATE TABLE IF NOT EXISTS "provider_portfolio_items" (
    "id" UUID DEFAULT gen_random_uuid(),
    "provider_profile_id" UUID NOT NULL,
    "image_url" TEXT NOT NULL,
    "title" VARCHAR(200),
    "description" TEXT,
    "created_at" TIMESTAMP DEFAULT now(),
    "updated_at" TIMESTAMP DEFAULT now(),
    PRIMARY KEY("id"),
    CONSTRAINT fk_port_provider FOREIGN KEY("provider_profile_id") REFERENCES "provider_profiles"("id") ON DELETE CASCADE
);
