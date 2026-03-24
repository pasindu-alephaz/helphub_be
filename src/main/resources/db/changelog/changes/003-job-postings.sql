-- liquibase formatted sql

-- changeset antigravity:31
CREATE SCHEMA IF NOT EXISTS extensions;
CREATE EXTENSION IF NOT EXISTS postgis SCHEMA extensions;

-- changeset antigravity:32
CREATE TABLE IF NOT EXISTS "jobs" (
	"id" UUID DEFAULT gen_random_uuid(),
	"title" VARCHAR(100) NOT NULL,
	"description" VARCHAR(500) NOT NULL,
	"subcategory_id" UUID,
	"location_address" TEXT NOT NULL,
	"location_coordinates" geometry(Point,4326) NOT NULL,
	"price" DECIMAL(10,2),
	"scheduled_at" TIMESTAMP,
	"urgency_flag" VARCHAR(20),
	"posted_by" UUID NOT NULL,
	"accepted_by" UUID,
	"status" VARCHAR(20) NOT NULL DEFAULT 'OPEN',
	"created_at" TIMESTAMP DEFAULT now(),
	"updated_at" TIMESTAMP DEFAULT now(),
	"deleted_at" TIMESTAMP,
	PRIMARY KEY("id"),
    CONSTRAINT fk_job_user FOREIGN KEY("posted_by") REFERENCES "users"("id"),
    CONSTRAINT fk_job_subcategory FOREIGN KEY("subcategory_id") REFERENCES "service_categories"("id"),
    CONSTRAINT fk_job_accepted_user FOREIGN KEY("accepted_by") REFERENCES "users"("id")
);


-- changeset antigravity:33
CREATE TABLE IF NOT EXISTS "job_templates" (
    "id" UUID DEFAULT gen_random_uuid(),
    "template_name" VARCHAR(100) NOT NULL,
    "title" VARCHAR(100) NOT NULL,
    "description" VARCHAR(500) NOT NULL,
    "subcategory_id" UUID,
    "location_address" TEXT NOT NULL,
    "location_coordinates" geometry(Point,4326) NOT NULL,
    "price" DECIMAL(10, 2),
    "urgency_flag" VARCHAR(20),
    "user_id" UUID NOT NULL,
    "created_at" TIMESTAMP DEFAULT now(),
    "updated_at" TIMESTAMP DEFAULT now(),
    PRIMARY KEY("id"),
    CONSTRAINT fk_job_template_user FOREIGN KEY("user_id") REFERENCES "users"("id"),
    CONSTRAINT fk_job_template_subcategory FOREIGN KEY("subcategory_id") REFERENCES "service_categories"("id")
);

-- changeset antigravity:34
CREATE TABLE IF NOT EXISTS "job_images" (
    "job_id" UUID NOT NULL,
    "image_id" UUID NOT NULL,
    PRIMARY KEY("job_id", "image_id"),
    CONSTRAINT fk_ji_job FOREIGN KEY("job_id") REFERENCES "jobs"("id") ON DELETE CASCADE,
    CONSTRAINT fk_ji_image FOREIGN KEY("image_id") REFERENCES "images"("id") ON DELETE CASCADE
);
