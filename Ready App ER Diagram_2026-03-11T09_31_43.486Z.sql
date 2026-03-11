

CREATE TABLE IF NOT EXISTS "images" (
	"id" UUID DEFAULT gen_random_uuid(),
	"user_id" UUID NOT NULL,
	"url" TEXT NOT NULL,
	"image_type" VARCHAR(50),
	"created_at" TIMESTAMP DEFAULT now(),
	"updated_at" TIMESTAMP DEFAULT now(),
	"deleted_at" TIMESTAMP DEFAULT NULL,
	PRIMARY KEY("id")
);




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




CREATE TABLE IF NOT EXISTS "password_resets" (
	"id" UUID DEFAULT gen_random_uuid(),
	"user_id" UUID NOT NULL,
	"token" VARCHAR(255) NOT NULL,
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



ALTER TABLE "images"
ADD FOREIGN KEY("user_id") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;
ALTER TABLE "verification_documents"
ADD FOREIGN KEY("user_id") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "verification_documents"
ADD FOREIGN KEY("image_id") REFERENCES "images"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "password_resets"
ADD FOREIGN KEY("user_id") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "user_roles"
ADD FOREIGN KEY("user_id") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "role_permissions"
ADD FOREIGN KEY("role_id") REFERENCES "roles"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "role_permissions"
ADD FOREIGN KEY("permission_id") REFERENCES "permissions"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;