-- HelpHub Database Schema (Updated from Migrations)
-- Generated from migrations: 001-014

-- ============================================
-- USERS & AUTHENTICATION
-- ============================================

CREATE TABLE IF NOT EXISTS "users" (
	"id" UUID DEFAULT gen_random_uuid(),
	"email" VARCHAR UNIQUE,
	"password_hash" VARCHAR NOT NULL,
	"full_name" VARCHAR(100),
	"phone_number" VARCHAR(20) UNIQUE,
	"display_name" VARCHAR(255),
	"about" TEXT,
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
	"birthday" DATE DEFAULT NULL,
	"identity_type" VARCHAR(20) DEFAULT NULL,
	"identity_value" VARCHAR(100) DEFAULT NULL,
	"language_preference" VARCHAR(20) DEFAULT 'SINHALA',
	"delete_reason" TEXT DEFAULT NULL,
	"last_verified_at" TIMESTAMP,
	"profile_picture_id" UUID,
	"identity_verification_id" UUID,
	PRIMARY KEY("id")
);

CREATE TABLE IF NOT EXISTS "images" (
	"id" UUID DEFAULT gen_random_uuid(),
	"user_id" UUID,
	"url" TEXT NOT NULL,
	"image_type" VARCHAR(50),
	"file_size" BIGINT DEFAULT NULL,
	"width" INTEGER DEFAULT NULL,
	"height" INTEGER DEFAULT NULL,
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

CREATE TABLE IF NOT EXISTS "phone_otps" (
    "id" UUID PRIMARY KEY,
    "phone_number" VARCHAR(20) NOT NULL,
    "otp" VARCHAR(6) NOT NULL,
    "purpose" VARCHAR(20) NOT NULL,
    "pending_token" VARCHAR(255),
    "expires_at" TIMESTAMP NOT NULL,
    "used_at" TIMESTAMP,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "refresh_tokens" (
    "id" BIGSERIAL PRIMARY KEY,
    "token" VARCHAR(512) NOT NULL UNIQUE,
    "user_id" UUID NOT NULL,
    "expires_at" TIMESTAMP NOT NULL,
    "revoked" BOOLEAN NOT NULL DEFAULT FALSE,
    "created_at" TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================
-- ROLES & PERMISSIONS
-- ============================================

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

-- ============================================
-- SERVICE CATEGORIES
-- ============================================

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

-- ============================================
-- JOBS & BIDDING
-- ============================================

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
	"job_type" VARCHAR(20),
	"preferred_price" DECIMAL(10,2),
	"job_availability_duration" VARCHAR(50),
	"job_plan" VARCHAR(100),
	"preferred_language" VARCHAR(50),
	"posted_by" UUID NOT NULL,
	"accepted_by" UUID,
	"status" VARCHAR(20) NOT NULL DEFAULT 'OPEN',
	"flagged" BOOLEAN NOT NULL DEFAULT FALSE,
	"flag_reason" TEXT,
	"archived_at" TIMESTAMP,
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
    "subcategory_id" UUID,
    "location_address" TEXT NOT NULL,
    "location_coordinates" geometry(Point,4326) NOT NULL,
    "price" DECIMAL(10, 2),
    "urgency_flag" VARCHAR(20),
    "job_type" VARCHAR(20),
    "preferred_price" DECIMAL(10,2),
    "job_availability_duration" VARCHAR(50),
    "job_plan" VARCHAR(100),
    "preferred_language" VARCHAR(50),
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

CREATE TABLE IF NOT EXISTS "bids" (
    "id" UUID DEFAULT gen_random_uuid(),
    "job_id" UUID NOT NULL,
    "provider_id" UUID NOT NULL,
    "amount" DECIMAL(10, 2) NOT NULL,
    "proposal" TEXT,
    "status" VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    "job_availability_duration" VARCHAR(50),
    "created_at" TIMESTAMP DEFAULT now(),
    "updated_at" TIMESTAMP DEFAULT now(),
    PRIMARY KEY("id")
);

CREATE TABLE IF NOT EXISTS "messages" (
    "id" UUID DEFAULT gen_random_uuid(),
    "job_id" UUID NOT NULL,
    "sender_id" UUID NOT NULL,
    "content" TEXT NOT NULL,
    "suggested_price" DECIMAL(10, 2),
    "suggested_scheduled_at" TIMESTAMP,
    "suggestion_status" VARCHAR(20),
    "suggested_availability_duration" VARCHAR(50),
    "created_at" TIMESTAMP DEFAULT now(),
    PRIMARY KEY("id")
);

-- ============================================
-- REVIEWS
-- ============================================

CREATE TABLE IF NOT EXISTS "job_reviews" (
    "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "job_id" UUID NOT NULL,
    "reviewer_id" UUID NOT NULL,
    "reviewed_user_id" UUID NOT NULL,
    "rating" INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    "comment" TEXT,
    "review_type" VARCHAR(20) NOT NULL,
    "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "job_review_media" (
    "job_review_id" UUID NOT NULL,
    "media_url" TEXT NOT NULL
);

-- ============================================
-- USER PROFILE ENHANCEMENTS
-- ============================================

CREATE TABLE IF NOT EXISTS "user_addresses" (
    "id" UUID DEFAULT gen_random_uuid(),
    "user_id" UUID NOT NULL,
    "label" VARCHAR(50) NOT NULL DEFAULT 'Home',
    "province" VARCHAR(100),
    "district" VARCHAR(100),
    "city" VARCHAR(100),
    "postal_code" VARCHAR(20),
    "country" VARCHAR(100),
    "latitude" DECIMAL(10, 8),
    "longitude" DECIMAL(11, 8),
    "is_default" BOOLEAN NOT NULL DEFAULT FALSE,
    "created_at" TIMESTAMP DEFAULT now(),
    "updated_at" TIMESTAMP DEFAULT now(),
    PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "user_languages" (
    "id" UUID DEFAULT gen_random_uuid(),
    "user_id" UUID NOT NULL,
    "language_code" VARCHAR(10),
    "language_name" VARCHAR(100) NOT NULL,
    "proficiency" VARCHAR(20) NOT NULL DEFAULT 'CONVERSATIONAL',
    "created_at" TIMESTAMP DEFAULT now(),
    "updated_at" TIMESTAMP DEFAULT now(),
    PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "user_educations" (
    "id" UUID DEFAULT gen_random_uuid(),
    "user_id" UUID NOT NULL,
    "educational_level" VARCHAR(100) NOT NULL,
    "certificates_id" UUID,
    "created_at" TIMESTAMP DEFAULT now(),
    "updated_at" TIMESTAMP DEFAULT now(),
    PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "user_professional_details" (
    "id" UUID DEFAULT gen_random_uuid(),
    "user_id" UUID NOT NULL UNIQUE,
    "skills" TEXT,
    "experience" TEXT,
    "created_at" TIMESTAMP DEFAULT now(),
    "updated_at" TIMESTAMP DEFAULT now(),
    PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "user_professional_categories" (
    "professional_detail_id" UUID NOT NULL,
    "category_id" UUID NOT NULL,
    PRIMARY KEY ("professional_detail_id", "category_id")
);

-- ============================================
-- PROVIDER ONBOARDING
-- ============================================

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
    "is_available" BOOLEAN DEFAULT TRUE,
    "created_at" TIMESTAMP DEFAULT now(),
    "updated_at" TIMESTAMP DEFAULT now(),
    "deleted_at" TIMESTAMP DEFAULT NULL,
    PRIMARY KEY("id")
);

CREATE TABLE IF NOT EXISTS "provider_identity_documents" (
    "id" UUID DEFAULT gen_random_uuid(),
    "provider_profile_id" UUID NOT NULL,
    "id_type" VARCHAR(20) NOT NULL,
    "id_number" VARCHAR(100) NOT NULL,
    "selfie_image_url" TEXT,
    "status" VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    "created_at" TIMESTAMP DEFAULT now(),
    "updated_at" TIMESTAMP DEFAULT now(),
    PRIMARY KEY("id")
);

CREATE TABLE IF NOT EXISTS "provider_identity_images" (
    "id" UUID PRIMARY KEY,
    "document_id" UUID NOT NULL,
    "url" VARCHAR(255) NOT NULL,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "provider_certificates" (
    "id" UUID DEFAULT gen_random_uuid(),
    "provider_profile_id" UUID NOT NULL,
    "name" VARCHAR(200) NOT NULL,
    "issued_date" DATE,
    "verified_at" TIMESTAMP,
    "created_at" TIMESTAMP DEFAULT now(),
    "updated_at" TIMESTAMP DEFAULT now(),
    PRIMARY KEY("id")
);

CREATE TABLE IF NOT EXISTS "provider_certificate_images" (
    "id" UUID PRIMARY KEY,
    "certificate_id" UUID NOT NULL,
    "url" VARCHAR(255) NOT NULL,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "provider_services" (
    "id" UUID DEFAULT gen_random_uuid(),
    "provider_profile_id" UUID NOT NULL,
    "category_id" UUID NOT NULL,
    "subcategory_id" UUID,
    "skill_level" VARCHAR(20) NOT NULL DEFAULT 'BEGINNER',
    "is_available" BOOLEAN DEFAULT TRUE,
    "start_date_time" TIMESTAMP,
    "end_date_time" TIMESTAMP,
    "created_at" TIMESTAMP DEFAULT now(),
    "updated_at" TIMESTAMP DEFAULT now(),
    PRIMARY KEY("id")
);

CREATE TABLE IF NOT EXISTS "provider_availability" (
    "id" UUID DEFAULT gen_random_uuid(),
    "provider_profile_id" UUID NOT NULL,
    "day_of_week" INTEGER NOT NULL,
    "start_time" TIME NOT NULL,
    "end_time" TIME NOT NULL,
    "created_at" TIMESTAMP DEFAULT now(),
    "updated_at" TIMESTAMP DEFAULT now(),
    PRIMARY KEY("id")
);

CREATE TABLE IF NOT EXISTS "provider_portfolio_items" (
    "id" UUID DEFAULT gen_random_uuid(),
    "provider_profile_id" UUID NOT NULL,
    "title" VARCHAR(200),
    "description" TEXT,
    "created_at" TIMESTAMP DEFAULT now(),
    "updated_at" TIMESTAMP DEFAULT now(),
    PRIMARY KEY("id")
);

CREATE TABLE IF NOT EXISTS "provider_portfolio_images" (
    "id" UUID PRIMARY KEY,
    "portfolio_item_id" UUID NOT NULL,
    "url" VARCHAR(255) NOT NULL,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- NOTIFICATIONS
-- ============================================

CREATE TABLE IF NOT EXISTS "notifications" (
    "id" UUID DEFAULT gen_random_uuid(),
    "user_id" UUID NOT NULL,
    "title" VARCHAR(255) NOT NULL,
    "message" TEXT NOT NULL,
    "payload" JSONB,
    "is_read" BOOLEAN DEFAULT false,
    "created_at" TIMESTAMP DEFAULT now(),
    "updated_at" TIMESTAMP DEFAULT now(),
    "deleted_at" TIMESTAMP DEFAULT NULL,
    PRIMARY KEY("id")
);

-- ============================================
-- FOREIGN KEYS
-- ============================================

-- Images
ALTER TABLE "images"
ADD FOREIGN KEY("user_id") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

-- Password Resets
ALTER TABLE "password_resets"
ADD FOREIGN KEY("user_id") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

-- Login OTPs
ALTER TABLE "login_otps"
ADD FOREIGN KEY("user_id") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

-- Verification Documents
ALTER TABLE "verification_documents"
ADD FOREIGN KEY("user_id") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE "verification_documents"
ADD FOREIGN KEY("image_id") REFERENCES "images"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

-- Role Permissions
ALTER TABLE "role_permissions"
ADD FOREIGN KEY("role_id") REFERENCES "roles"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE "role_permissions"
ADD FOREIGN KEY("permission_id") REFERENCES "permissions"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

-- User Roles
ALTER TABLE "user_roles"
ADD FOREIGN KEY("user_id") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE "user_roles"
ADD FOREIGN KEY("role_id") REFERENCES "roles"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

-- Refresh Tokens
ALTER TABLE "refresh_tokens"
ADD FOREIGN KEY("user_id") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

-- Service Categories
ALTER TABLE "service_categories"
ADD FOREIGN KEY("parent_id") REFERENCES "service_categories"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE "service_categories"
ADD FOREIGN KEY("icon_id") REFERENCES "images"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

-- Jobs
ALTER TABLE "jobs"
ADD FOREIGN KEY("posted_by") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE "jobs"
ADD FOREIGN KEY("accepted_by") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE "jobs"
ADD FOREIGN KEY("subcategory_id") REFERENCES "service_categories"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

-- Job Templates
ALTER TABLE "job_templates"
ADD FOREIGN KEY("user_id") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE "job_templates"
ADD FOREIGN KEY("subcategory_id") REFERENCES "service_categories"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

-- Job Images
ALTER TABLE "job_images"
ADD FOREIGN KEY("job_id") REFERENCES "jobs"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE "job_images"
ADD FOREIGN KEY("image_id") REFERENCES "images"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

-- Bids
ALTER TABLE "bids"
ADD FOREIGN KEY("job_id") REFERENCES "jobs"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE "bids"
ADD FOREIGN KEY("provider_id") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

-- Messages
ALTER TABLE "messages"
ADD FOREIGN KEY("job_id") REFERENCES "jobs"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE "messages"
ADD FOREIGN KEY("sender_id") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

-- Job Reviews
ALTER TABLE "job_reviews"
ADD FOREIGN KEY("job_id") REFERENCES "jobs"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE "job_reviews"
ADD FOREIGN KEY("reviewer_id") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE "job_reviews"
ADD FOREIGN KEY("reviewed_user_id") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

-- Job Review Media
ALTER TABLE "job_review_media"
ADD FOREIGN KEY("job_review_id") REFERENCES "job_reviews"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

-- User Addresses
ALTER TABLE "user_addresses"
ADD FOREIGN KEY("user_id") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

-- User Languages
ALTER TABLE "user_languages"
ADD FOREIGN KEY("user_id") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

-- User Educations
ALTER TABLE "user_educations"
ADD FOREIGN KEY("user_id") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE "user_educations"
ADD FOREIGN KEY("certificates_id") REFERENCES "images"("id")
ON UPDATE NO ACTION ON DELETE SET NULL;

-- User Professional Details
ALTER TABLE "user_professional_details"
ADD FOREIGN KEY("user_id") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

-- User Professional Categories
ALTER TABLE "user_professional_categories"
ADD FOREIGN KEY("professional_detail_id") REFERENCES "user_professional_details"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE "user_professional_categories"
ADD FOREIGN KEY("category_id") REFERENCES "service_categories"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

-- User profile picture and identity verification
ALTER TABLE "users"
ADD FOREIGN KEY("profile_picture_id") REFERENCES "images"("id")
ON UPDATE NO ACTION ON DELETE SET NULL;

ALTER TABLE "users"
ADD FOREIGN KEY("identity_verification_id") REFERENCES "images"("id")
ON UPDATE NO ACTION ON DELETE SET NULL;

-- Provider Profiles
ALTER TABLE "provider_profiles"
ADD FOREIGN KEY("user_id") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

-- Provider Identity Documents
ALTER TABLE "provider_identity_documents"
ADD FOREIGN KEY("provider_profile_id") REFERENCES "provider_profiles"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

-- Provider Identity Images
ALTER TABLE "provider_identity_images"
ADD FOREIGN KEY("document_id") REFERENCES "provider_identity_documents"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

-- Provider Certificates
ALTER TABLE "provider_certificates"
ADD FOREIGN KEY("provider_profile_id") REFERENCES "provider_profiles"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

-- Provider Certificate Images
ALTER TABLE "provider_certificate_images"
ADD FOREIGN KEY("certificate_id") REFERENCES "provider_certificates"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

-- Provider Services
ALTER TABLE "provider_services"
ADD FOREIGN KEY("provider_profile_id") REFERENCES "provider_profiles"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE "provider_services"
ADD FOREIGN KEY("category_id") REFERENCES "service_categories"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE "provider_services"
ADD FOREIGN KEY("subcategory_id") REFERENCES "service_categories"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

-- Provider Availability
ALTER TABLE "provider_availability"
ADD FOREIGN KEY("provider_profile_id") REFERENCES "provider_profiles"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

-- Provider Portfolio Items
ALTER TABLE "provider_portfolio_items"
ADD FOREIGN KEY("provider_profile_id") REFERENCES "provider_profiles"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

-- Provider Portfolio Images
ALTER TABLE "provider_portfolio_images"
ADD FOREIGN KEY("portfolio_item_id") REFERENCES "provider_portfolio_items"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

-- Notifications
ALTER TABLE "notifications"
ADD FOREIGN KEY("user_id") REFERENCES "users"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;

-- ============================================
-- INDEXES
-- ============================================

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_job_reviews_job_id ON job_reviews(job_id);
CREATE INDEX IF NOT EXISTS idx_job_reviews_reviewer_id ON job_reviews(reviewer_id);
CREATE INDEX IF NOT EXISTS idx_job_reviews_reviewed_user_id ON job_reviews(reviewed_user_id);
CREATE INDEX IF NOT EXISTS idx_job_review_media_review_id ON job_review_media(job_review_id);
