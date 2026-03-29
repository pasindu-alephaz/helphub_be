-- liquibase formatted sql

-- changeset helphub:019-provider-new-schema
CREATE TABLE IF NOT EXISTS "provider_profiles" (
    "id" UUID PRIMARY KEY,
    "user_id" UUID NOT NULL UNIQUE,
    "bio" TEXT,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "fk_provider_user" FOREIGN KEY ("user_id") REFERENCES "users"("id") ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "provider_identity_documents" (
    "id" UUID PRIMARY KEY,
    "provider_profile_id" UUID NOT NULL,
    "document_type" VARCHAR(50) NOT NULL,
    "issuing_country" VARCHAR(100),
    "document_code" VARCHAR(100),
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "fk_identity_doc_provider" FOREIGN KEY ("provider_profile_id") REFERENCES "provider_profiles"("id") ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "provider_identity_images" (
    "id" UUID PRIMARY KEY,
    "identity_document_id" UUID NOT NULL,
    "file_url" VARCHAR(255) NOT NULL,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "fk_identity_img_doc" FOREIGN KEY ("identity_document_id") REFERENCES "provider_identity_documents"("id") ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "provider_skills" (
    "id" UUID PRIMARY KEY,
    "provider_profile_id" UUID NOT NULL,
    "subcategory_id" UUID NOT NULL,
    "skill_level" VARCHAR(50),
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "fk_skill_provider" FOREIGN KEY ("provider_profile_id") REFERENCES "provider_profiles"("id") ON DELETE CASCADE,
    CONSTRAINT "fk_skill_category" FOREIGN KEY ("subcategory_id") REFERENCES "service_categories"("id") ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "provider_skill_proofs" (
    "id" UUID PRIMARY KEY,
    "provider_profile_id" UUID NOT NULL,
    "subcategory_id" UUID,
    "title" VARCHAR(255),
    "description" TEXT,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "fk_skill_proof_provider" FOREIGN KEY ("provider_profile_id") REFERENCES "provider_profiles"("id") ON DELETE CASCADE,
    CONSTRAINT "fk_skill_proof_category" FOREIGN KEY ("subcategory_id") REFERENCES "service_categories"("id") ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS "provider_skill_proof_images" (
    "id" UUID PRIMARY KEY,
    "skill_proof_id" UUID NOT NULL,
    "file_url" VARCHAR(255) NOT NULL,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "fk_proof_img_proof" FOREIGN KEY ("skill_proof_id") REFERENCES "provider_skill_proofs"("id") ON DELETE CASCADE
);

ALTER TABLE "user_addresses" ADD COLUMN IF NOT EXISTS "location" geometry(Point, 4326);
