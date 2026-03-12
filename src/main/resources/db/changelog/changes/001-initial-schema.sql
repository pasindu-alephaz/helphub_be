-- liquibase formatted sql

-- changeset antigravity:1
CREATE TABLE IF NOT EXISTS "users" (
	"id" UUID DEFAULT gen_random_uuid(),
	"email" VARCHAR NOT NULL UNIQUE,
	"password_hash" VARCHAR NOT NULL,
	"first_name" VARCHAR(100),
	"last_name" VARCHAR(100),
	"phone_number" VARCHAR(20),
	"bio" TEXT,
	"avatar_id" UUID,
	"user_type" VARCHAR(20) DEFAULT 'customer',
	"verified_at" TIMESTAMP,
	"status" VARCHAR(20) DEFAULT 'active',
	"created_at" TIMESTAMP DEFAULT now(),
	"updated_at" TIMESTAMP DEFAULT now(),
	"deleted_at" TIMESTAMP DEFAULT NULL,
	PRIMARY KEY("id")
);

-- changeset antigravity:2
CREATE TABLE IF NOT EXISTS "images" (
	"id" UUID DEFAULT gen_random_uuid(),
	"user_id" UUID NOT NULL,
	"url" TEXT NOT NULL,
	"image_type" VARCHAR(50),
	"created_at" TIMESTAMP DEFAULT now(),
	"updated_at" TIMESTAMP DEFAULT now(),
	"deleted_at" TIMESTAMP DEFAULT NULL,
	PRIMARY KEY("id"),
	CONSTRAINT fk_image_user FOREIGN KEY("user_id") REFERENCES "users"("id") ON DELETE CASCADE
);

-- changeset antigravity:3
CREATE TABLE IF NOT EXISTS "password_resets" (
	"id" UUID DEFAULT gen_random_uuid(),
	"user_id" UUID NOT NULL,
	"otp" VARCHAR(255) NOT NULL,
	"expires_at" TIMESTAMP NOT NULL,
	"used_at" TIMESTAMP,
	"created_at" TIMESTAMP DEFAULT now(),
	"updated_at" TIMESTAMP DEFAULT now(),
	"deleted_at" TIMESTAMP DEFAULT NULL,
	PRIMARY KEY("id"),
	CONSTRAINT fk_password_reset_user FOREIGN KEY("user_id") REFERENCES "users"("id")
);

-- changeset antigravity:4
CREATE TABLE IF NOT EXISTS "roles" (
	"id" SERIAL,
	"name" VARCHAR(50) NOT NULL UNIQUE,
	"created_at" TIMESTAMP DEFAULT now(),
	"updated_at" TIMESTAMP DEFAULT now(),
	"deleted_at" TIMESTAMP DEFAULT NULL,
	PRIMARY KEY("id")
);

-- changeset antigravity:5
CREATE TABLE IF NOT EXISTS "permissions" (
	"id" SERIAL,
	"slug" VARCHAR(100) NOT NULL UNIQUE,
	"created_at" TIMESTAMP DEFAULT now(),
	"updated_at" TIMESTAMP DEFAULT now(),
	"deleted_at" TIMESTAMP DEFAULT NULL,
	PRIMARY KEY("id")
);

-- changeset antigravity:6
CREATE TABLE IF NOT EXISTS "role_permissions" (
	"role_id" INTEGER,
	"permission_id" INTEGER,
	"created_at" TIMESTAMP DEFAULT now(),
	"updated_at" TIMESTAMP DEFAULT now(),
	"deleted_at" TIMESTAMP DEFAULT NULL,
	PRIMARY KEY("role_id", "permission_id"),
	CONSTRAINT fk_rp_role FOREIGN KEY("role_id") REFERENCES "roles"("id"),
	CONSTRAINT fk_rp_permission FOREIGN KEY("permission_id") REFERENCES "permissions"("id")
);

-- changeset antigravity:7
CREATE TABLE IF NOT EXISTS "user_roles" (
	"user_id" UUID,
	"role_id" INTEGER,
	"created_at" TIMESTAMP DEFAULT now(),
	"updated_at" TIMESTAMP DEFAULT now(),
	"deleted_at" TIMESTAMP DEFAULT NULL,
	PRIMARY KEY("user_id", "role_id"),
	CONSTRAINT fk_ur_user FOREIGN KEY("user_id") REFERENCES "users"("id"),
	CONSTRAINT fk_ur_role FOREIGN KEY("role_id") REFERENCES "roles"("id")
);

-- changeset antigravity:8
CREATE TABLE IF NOT EXISTS "verification_documents" (
	"id" UUID NOT NULL UNIQUE,
	"user_id" UUID,
	"image_id" UUID,
	"status" VARCHAR(20),
	"created_at" TIMESTAMP,
	"updated_at" TIMESTAMP,
	"deleted_at" TIMESTAMP,
	PRIMARY KEY("id"),
	CONSTRAINT fk_vd_user FOREIGN KEY("user_id") REFERENCES "users"("id"),
	CONSTRAINT fk_vd_image FOREIGN KEY("image_id") REFERENCES "images"("id")
);

-- changeset antigravity:9
CREATE TABLE IF NOT EXISTS "verification_otps" (
	"id" UUID DEFAULT gen_random_uuid(),
	"token" VARCHAR(64) NOT NULL UNIQUE,
	"otp" VARCHAR(6) NOT NULL,
	"type" VARCHAR(10) NOT NULL,
	"target" VARCHAR(255) NOT NULL,
	"expires_at" TIMESTAMP NOT NULL,
	"used_at" TIMESTAMP,
	"created_at" TIMESTAMP DEFAULT now(),
	"updated_at" TIMESTAMP DEFAULT now(),
	"deleted_at" TIMESTAMP DEFAULT NULL,
	PRIMARY KEY("id")
);

-- changeset antigravity:10
ALTER TABLE "users" ADD COLUMN IF NOT EXISTS "email_verified_at" TIMESTAMP DEFAULT NULL;
ALTER TABLE "users" ADD COLUMN IF NOT EXISTS "phone_verified_at" TIMESTAMP DEFAULT NULL;
