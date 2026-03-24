-- liquibase formatted sql

-- changeset helphub:006-identity-fields
ALTER TABLE "users" ADD COLUMN IF NOT EXISTS "identity_type" VARCHAR(20) DEFAULT NULL;
ALTER TABLE "users" ADD COLUMN IF NOT EXISTS "identity_value" VARCHAR(100) DEFAULT NULL;

-- changeset helphub:006-language-preference
ALTER TABLE "users" ADD COLUMN IF NOT EXISTS "language_preference" VARCHAR(20) DEFAULT 'SINHALA';

-- changeset helphub:006-delete-reason
ALTER TABLE "users" ADD COLUMN IF NOT EXISTS "delete_reason" TEXT DEFAULT NULL;

-- changeset helphub:006-cleanup-duplicate-phones
DELETE FROM "users"
WHERE "id" IN (
    SELECT "id"
    FROM (
        SELECT "id",
               ROW_NUMBER() OVER(PARTITION BY "phone_number" ORDER BY "created_at" DESC, "id" DESC) as "rn"
        FROM "users"
        WHERE "phone_number" IS NOT NULL
    ) t
    WHERE "rn" > 1
);

-- changeset helphub:006-phone-unique-constraint
ALTER TABLE "users" ADD CONSTRAINT uq_users_phone_number UNIQUE ("phone_number");

-- changeset helphub:006-user-addresses
CREATE TABLE IF NOT EXISTS "user_addresses" (
    "id"          UUID DEFAULT gen_random_uuid(),
    "user_id"     UUID NOT NULL,
    "label"       VARCHAR(50) NOT NULL DEFAULT 'Home',
    "province"    VARCHAR(100),
    "district"    VARCHAR(100),
    "city"        VARCHAR(100),
    "postal_code" VARCHAR(20),
    "latitude"    DECIMAL(10, 8),
    "longitude"   DECIMAL(11, 8),
    "is_default"  BOOLEAN NOT NULL DEFAULT FALSE,
    "created_at"  TIMESTAMP DEFAULT now(),
    "updated_at"  TIMESTAMP DEFAULT now(),
    PRIMARY KEY ("id"),
    CONSTRAINT fk_address_user FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE
);

-- changeset helphub:006-user-languages
CREATE TABLE IF NOT EXISTS "user_languages" (
    "id"            UUID DEFAULT gen_random_uuid(),
    "user_id"       UUID NOT NULL,
    "language_code" VARCHAR(10),
    "language_name" VARCHAR(100) NOT NULL,
    "proficiency"   VARCHAR(20) NOT NULL DEFAULT 'CONVERSATIONAL',
    "created_at"    TIMESTAMP DEFAULT now(),
    "updated_at"    TIMESTAMP DEFAULT now(),
    PRIMARY KEY ("id"),
    CONSTRAINT fk_lang_user FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE
);

-- changeset helphub:006-images-metadata
ALTER TABLE "images" ADD COLUMN IF NOT EXISTS "file_size" BIGINT DEFAULT NULL;
ALTER TABLE "images" ADD COLUMN IF NOT EXISTS "width" INTEGER DEFAULT NULL;
ALTER TABLE "images" ADD COLUMN IF NOT EXISTS "height" INTEGER DEFAULT NULL;
