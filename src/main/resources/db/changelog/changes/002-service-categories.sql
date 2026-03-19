-- liquibase formatted sql

-- changeset antigravity:21
ALTER TABLE "images" ALTER COLUMN "user_id" DROP NOT NULL;

-- changeset antigravity:22
CREATE TABLE IF NOT EXISTS "service_categories" (
    "id" UUID DEFAULT gen_random_uuid(),
    "name" JSONB NOT NULL,
    "description" JSONB,
    "status" VARCHAR(20) DEFAULT 'active',
    "display_order" INTEGER DEFAULT 0,
    "parent_id" UUID,
    "icon_id" UUID,
    "created_at" TIMESTAMP DEFAULT now(),
    "updated_at" TIMESTAMP DEFAULT now(),
    "deleted_at" TIMESTAMP DEFAULT NULL,
    PRIMARY KEY("id"),
    CONSTRAINT fk_category_parent FOREIGN KEY("parent_id") REFERENCES "service_categories"("id"),
    CONSTRAINT fk_category_icon FOREIGN KEY("icon_id") REFERENCES "images"("id")
);
