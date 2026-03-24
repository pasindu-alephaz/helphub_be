-- liquibase formatted sql

-- changeset antigravity:101
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
    PRIMARY KEY("id"),
    CONSTRAINT fk_notification_user FOREIGN KEY("user_id") REFERENCES "users"("id") ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON "notifications"("user_id");
