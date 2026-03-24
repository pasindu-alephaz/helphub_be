-- liquibase formatted sql

-- changeset antigravity:41
CREATE TABLE IF NOT EXISTS "bids" (
    "id" UUID DEFAULT gen_random_uuid(),
    "job_id" UUID NOT NULL,
    "provider_id" UUID NOT NULL,
    "amount" DECIMAL(10, 2) NOT NULL,
    "proposal" TEXT,
    "status" VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    "created_at" TIMESTAMP DEFAULT now(),
    "updated_at" TIMESTAMP DEFAULT now(),
    PRIMARY KEY("id"),
    CONSTRAINT fk_bid_job FOREIGN KEY("job_id") REFERENCES "jobs"("id") ON DELETE CASCADE,
    CONSTRAINT fk_bid_provider FOREIGN KEY("provider_id") REFERENCES "users"("id") ON DELETE CASCADE
);

-- changeset antigravity:42
CREATE TABLE IF NOT EXISTS "messages" (
    "id" UUID DEFAULT gen_random_uuid(),
    "job_id" UUID NOT NULL,
    "sender_id" UUID NOT NULL,
    "content" TEXT NOT NULL,
    "suggested_price" DECIMAL(10, 2),
    "suggested_scheduled_at" TIMESTAMP,
    "suggestion_status" VARCHAR(20),
    "created_at" TIMESTAMP DEFAULT now(),
    PRIMARY KEY("id"),
    CONSTRAINT fk_message_job FOREIGN KEY("job_id") REFERENCES "jobs"("id") ON DELETE CASCADE,
    CONSTRAINT fk_message_sender FOREIGN KEY("sender_id") REFERENCES "users"("id") ON DELETE CASCADE
);
