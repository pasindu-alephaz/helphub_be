-- liquibase formatted sql

-- changeset antigravity:51
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS flagged BOOLEAN NOT NULL DEFAULT FALSE;

-- changeset antigravity:52
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS flag_reason TEXT;

-- changeset antigravity:53
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS archived_at TIMESTAMP;
