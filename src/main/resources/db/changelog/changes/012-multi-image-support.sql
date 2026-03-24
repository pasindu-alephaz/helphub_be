-- liquibase formatted sql

-- changeset helphub:012-multi-image-support-1
ALTER TABLE "provider_identity_documents" DROP COLUMN IF EXISTS "front_image_url";
ALTER TABLE "provider_identity_documents" DROP COLUMN IF EXISTS "back_image_url";

CREATE TABLE "provider_identity_images" (
    "id" UUID PRIMARY KEY,
    "document_id" UUID NOT NULL,
    "url" VARCHAR(255) NOT NULL,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "fk_pi_images_doc" FOREIGN KEY ("document_id") REFERENCES "provider_identity_documents"("id") ON DELETE CASCADE
);

-- changeset helphub:012-multi-image-support-2
ALTER TABLE "provider_certificates" DROP COLUMN IF EXISTS "file_url";

CREATE TABLE "provider_certificate_images" (
    "id" UUID PRIMARY KEY,
    "certificate_id" UUID NOT NULL,
    "url" VARCHAR(255) NOT NULL,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "fk_pc_images_cert" FOREIGN KEY ("certificate_id") REFERENCES "provider_certificates"("id") ON DELETE CASCADE
);

-- changeset helphub:012-multi-image-support-3
ALTER TABLE "provider_portfolio_items" DROP COLUMN IF EXISTS "image_url";

CREATE TABLE "provider_portfolio_images" (
    "id" UUID PRIMARY KEY,
    "portfolio_item_id" UUID NOT NULL,
    "url" VARCHAR(255) NOT NULL,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "fk_pp_images_item" FOREIGN KEY ("portfolio_item_id") REFERENCES "provider_portfolio_items"("id") ON DELETE CASCADE
);
