-- liquibase formatted sql

-- changeset antigravity:15-1
CREATE TABLE IF NOT EXISTS "job_time_sessions" (
    "id" UUID DEFAULT gen_random_uuid(),
    "job_id" UUID NOT NULL,
    "session_number" INTEGER NOT NULL,
    "started_at" TIMESTAMP NOT NULL DEFAULT now(),
    "ended_at" TIMESTAMP,
    "total_minutes" INTEGER DEFAULT 0,
    "pause_reason" VARCHAR(255),
    "otp_verified" BOOLEAN DEFAULT FALSE,
    "created_at" TIMESTAMP NOT NULL DEFAULT now(),
    "updated_at" TIMESTAMP NOT NULL DEFAULT now(),
    PRIMARY KEY("id"),
    CONSTRAINT fk_session_job FOREIGN KEY("job_id") REFERENCES "jobs"("id")
);

-- changeset antigravity:15-2
CREATE TABLE IF NOT EXISTS "job_otp_requests" (
    "id" UUID DEFAULT gen_random_uuid(),
    "job_id" UUID NOT NULL,
    "session_id" UUID,
    "otp_code" VARCHAR(10) NOT NULL,
    "purpose" VARCHAR(20) NOT NULL,
    "status" VARCHAR(20) NOT NULL,
    "attempts" INTEGER DEFAULT 0,
    "expires_at" TIMESTAMP NOT NULL,
    "requested_by" UUID NOT NULL,
    "verified_by" UUID,
    "verified_at" TIMESTAMP,
    "created_at" TIMESTAMP NOT NULL DEFAULT now(),
    "updated_at" TIMESTAMP NOT NULL DEFAULT now(),
    PRIMARY KEY("id"),
    CONSTRAINT fk_otp_job FOREIGN KEY("job_id") REFERENCES "jobs"("id"),
    CONSTRAINT fk_otp_session FOREIGN KEY("session_id") REFERENCES "job_time_sessions"("id"),
    CONSTRAINT fk_otp_requested_by FOREIGN KEY("requested_by") REFERENCES "users"("id"),
    CONSTRAINT fk_otp_verified_by FOREIGN KEY("verified_by") REFERENCES "users"("id")
);

-- changeset antigravity:15-3
ALTER TABLE "jobs" ADD COLUMN IF NOT EXISTS "current_session_id" UUID;
ALTER TABLE "jobs" ADD COLUMN IF NOT EXISTS "total_work_minutes" INTEGER DEFAULT 0;
ALTER TABLE "jobs" ADD CONSTRAINT fk_job_current_session FOREIGN KEY("current_session_id") REFERENCES "job_time_sessions"("id");
