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
	"is_2fa_enabled" BOOLEAN DEFAULT false,
	"email_verified_at" TIMESTAMP DEFAULT NULL,
	"phone_verified_at" TIMESTAMP DEFAULT NULL,
	"google_id" VARCHAR(255) DEFAULT NULL,
	"apple_id" VARCHAR(255) DEFAULT NULL,
	"profile_image_url" TEXT DEFAULT NULL,
	PRIMARY KEY("id")
);

CREATE TABLE IF NOT EXISTS "images" (
	"id" UUID DEFAULT gen_random_uuid(),
	"user_id" UUID,
	"url" TEXT NOT NULL,
	"image_type" VARCHAR(50),
	"created_at" TIMESTAMP DEFAULT now(),
	"updated_at" TIMESTAMP DEFAULT now(),
	"deleted_at" TIMESTAMP DEFAULT NULL,
	PRIMARY KEY("id")
);

CREATE TABLE IF NOT EXISTS "password_resets" (
	"id" UUID DEFAULT gen_random_uuid(),
	"user_id" UUID NOT NULL,
	"otp" VARCHAR(255) NOT NULL,
	"expires_at" TIMESTAMP NOT NULL,
	"used_at" TIMESTAMP,
	"created_at" TIMESTAMP DEFAULT now(),
	"updated_at" TIMESTAMP DEFAULT now(),
	"deleted_at" TIMESTAMP DEFAULT NULL,
	PRIMARY KEY("id")
);

CREATE TABLE IF NOT EXISTS "roles" (
	"id" SERIAL,
	"name" VARCHAR(50) NOT NULL UNIQUE,
	"created_at" TIMESTAMP DEFAULT now(),
	"updated_at" TIMESTAMP DEFAULT now(),
	"deleted_at" TIMESTAMP DEFAULT NULL,
	PRIMARY KEY("id")
);

CREATE TABLE IF NOT EXISTS "permissions" (
	"id" SERIAL,
	"slug" VARCHAR(100) NOT NULL UNIQUE,
	"created_at" TIMESTAMP DEFAULT now(),
	"updated_at" TIMESTAMP DEFAULT now(),
	"deleted_at" TIMESTAMP DEFAULT NULL,
	PRIMARY KEY("id")
);

CREATE TABLE IF NOT EXISTS "role_permissions" (
	"role_id" INTEGER,
	"permission_id" INTEGER,
	"created_at" TIMESTAMP DEFAULT now(),
	"updated_at" TIMESTAMP DEFAULT now(),
	"deleted_at" TIMESTAMP DEFAULT NULL,
	PRIMARY KEY("role_id", "permission_id")
);

CREATE TABLE IF NOT EXISTS "user_roles" (
	"user_id" UUID,
	"role_id" INTEGER,
	"created_at" TIMESTAMP DEFAULT now(),
	"updated_at" TIMESTAMP DEFAULT now(),
	"deleted_at" TIMESTAMP DEFAULT NULL,
	PRIMARY KEY("user_id", "role_id")
);

CREATE TABLE IF NOT EXISTS "verification_documents" (
	"id" UUID NOT NULL UNIQUE,
	"user_id" UUID,
	"image_id" UUID,
	"status" VARCHAR(20),
	"created_at" TIMESTAMP,
	"updated_at" TIMESTAMP,
	"deleted_at" TIMESTAMP,
	PRIMARY KEY("id")
);

CREATE TABLE IF NOT EXISTS "login_otps" (
	"id" UUID DEFAULT gen_random_uuid(),
	"user_id" UUID NOT NULL,
	"otp" VARCHAR(6) NOT NULL,
	"expires_at" TIMESTAMP NOT NULL,
	"used_at" TIMESTAMP,
	"created_at" TIMESTAMP DEFAULT now(),
	PRIMARY KEY("id")
);

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
    PRIMARY KEY("id")
);

CREATE TABLE IF NOT EXISTS "jobs" (
    "id" UUID DEFAULT gen_random_uuid(),
    "title" VARCHAR(100) NOT NULL,
    "description" VARCHAR(500) NOT NULL,
    "category_id" UUID NOT NULL,
    "subcategory_id" UUID,
    "location_address" TEXT NOT NULL,
    "location_latitude" DOUBLE PRECISION NOT NULL,
    "location_longitude" DOUBLE PRECISION NOT NULL,
    "price" DECIMAL(10, 2),
    "scheduled_date" DATE,
    "scheduled_time" TIME,
    "urgency_flag" VARCHAR(20),
    "recurring_type" VARCHAR(20),
    "recurring_end_date" DATE,
    "user_id" UUID NOT NULL,
    "status" VARCHAR(20) DEFAULT 'OPEN' NOT NULL,
    "created_at" TIMESTAMP DEFAULT now(),
    "updated_at" TIMESTAMP DEFAULT now(),
    "deleted_at" TIMESTAMP,
    PRIMARY KEY("id")
);

CREATE TABLE IF NOT EXISTS "job_templates" (
    "id" UUID DEFAULT gen_random_uuid(),
    "template_name" VARCHAR(100) NOT NULL,
    "title" VARCHAR(100) NOT NULL,
    "description" VARCHAR(500) NOT NULL,
    "category_id" UUID NOT NULL,
    "subcategory_id" UUID,
    "location_address" TEXT NOT NULL,
    "location_latitude" DOUBLE PRECISION NOT NULL,
    "location_longitude" DOUBLE PRECISION NOT NULL,
    "price" DECIMAL(10, 2),
    "urgency_flag" VARCHAR(20),
    "recurring_type" VARCHAR(20),
    "user_id" UUID NOT NULL,
    "created_at" TIMESTAMP DEFAULT now(),
    "updated_at" TIMESTAMP DEFAULT now(),
    PRIMARY KEY("id")
);

CREATE TABLE IF NOT EXISTS "job_images" (
    "job_id" UUID NOT NULL,
    "image_id" UUID NOT NULL,
    PRIMARY KEY("job_id", "image_id")
);

ALTER TABLE "images"
ADD FOREIGN KEY("user_id") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE "password_resets"
ADD FOREIGN KEY("user_id") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE "role_permissions"
ADD FOREIGN KEY("role_id") REFERENCES "roles"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE "role_permissions"
ADD FOREIGN KEY("permission_id") REFERENCES "permissions"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE "user_roles"
ADD FOREIGN KEY("user_id") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE "user_roles"
ADD FOREIGN KEY("role_id") REFERENCES "roles"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE "verification_documents"
ADD FOREIGN KEY("user_id") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE "verification_documents"
ADD FOREIGN KEY("image_id") REFERENCES "images"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE "login_otps"
ADD FOREIGN KEY("user_id") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE "service_categories"
ADD FOREIGN KEY("parent_id") REFERENCES "service_categories"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE "service_categories"
ADD FOREIGN KEY("icon_id") REFERENCES "images"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE "jobs"
ADD FOREIGN KEY("user_id") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE "jobs"
ADD FOREIGN KEY("category_id") REFERENCES "service_categories"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE "jobs"
ADD FOREIGN KEY("subcategory_id") REFERENCES "service_categories"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE "job_templates"
ADD FOREIGN KEY("user_id") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE "job_templates"
ADD FOREIGN KEY("category_id") REFERENCES "service_categories"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE "job_templates"
ADD FOREIGN KEY("subcategory_id") REFERENCES "service_categories"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE "job_images"
ADD FOREIGN KEY("job_id") REFERENCES "jobs"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE "job_images"
ADD FOREIGN KEY("image_id") REFERENCES "images"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;