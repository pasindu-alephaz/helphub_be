-- liquibase formatted sql

-- changeset helphub:011-provider-services-availability-1
ALTER TABLE "provider_services" ADD COLUMN IF NOT EXISTS "is_available" BOOLEAN DEFAULT TRUE;
ALTER TABLE "provider_services" ADD COLUMN IF NOT EXISTS "start_date_time" TIMESTAMP;
ALTER TABLE "provider_services" ADD COLUMN IF NOT EXISTS "end_date_time" TIMESTAMP;
