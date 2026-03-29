-- liquibase formatted sql

-- changeset helphub:020-fix-provider-inheritance-stale-tables
DROP TABLE IF EXISTS "provider_identity_images" CASCADE;
DROP TABLE IF EXISTS "provider_portfolio_images" CASCADE;

CREATE TABLE "provider_identity_images" (
    "id" UUID PRIMARY KEY,
    "identity_document_id" UUID NOT NULL,
    "file_url" VARCHAR(255) NOT NULL,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "fk_identity_img_doc" FOREIGN KEY ("identity_document_id") REFERENCES "provider_identity_documents"("id") ON DELETE CASCADE
);
