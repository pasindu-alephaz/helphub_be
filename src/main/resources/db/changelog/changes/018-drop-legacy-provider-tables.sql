-- liquibase formatted sql

-- changeset helphub:018-drop-legacy-provider-tables
DROP TABLE IF EXISTS "provider_skill_proofs" CASCADE;
DROP TABLE IF EXISTS "provider_services" CASCADE;
DROP TABLE IF EXISTS "provider_portfolio_items" CASCADE;
DROP TABLE IF EXISTS "provider_identity_documents" CASCADE;
DROP TABLE IF EXISTS "provider_profiles" CASCADE;
