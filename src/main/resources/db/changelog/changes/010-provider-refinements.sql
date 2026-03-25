-- liquibase formatted sql

-- changeset helphub:010-provider-refinements-1
ALTER TABLE "provider_profiles" ADD COLUMN IF NOT EXISTS "is_available" BOOLEAN DEFAULT TRUE;

-- changeset helphub:010-provider-refinements-2
ALTER TABLE "provider_services" ADD COLUMN IF NOT EXISTS "subcategory_id" UUID;
ALTER TABLE "provider_services" ADD CONSTRAINT fk_ps_subcategory FOREIGN KEY ("subcategory_id") REFERENCES "service_categories"("id") ON DELETE CASCADE;
