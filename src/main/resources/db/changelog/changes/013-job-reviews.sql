-- liquibase formatted sql

-- changeset antigravity:45
CREATE TABLE IF NOT EXISTS job_reviews (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_id UUID NOT NULL,
    reviewer_id UUID NOT NULL,
    reviewed_user_id UUID NOT NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    review_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_job_reviews_job FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    CONSTRAINT fk_job_reviews_reviewer FOREIGN KEY (reviewer_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_job_reviews_reviewed_user FOREIGN KEY (reviewed_user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_review_type CHECK (review_type IN ('PROVIDER', 'USER'))
);

-- changeset antigravity:46
CREATE INDEX IF NOT EXISTS idx_job_reviews_job_id ON job_reviews(job_id);

-- changeset antigravity:47
CREATE INDEX IF NOT EXISTS idx_job_reviews_reviewer_id ON job_reviews(reviewer_id);

-- changeset antigravity:48
CREATE INDEX IF NOT EXISTS idx_job_reviews_reviewed_user_id ON job_reviews(reviewed_user_id);

-- changeset antigravity:49
CREATE TABLE IF NOT EXISTS job_review_media (
    job_review_id UUID NOT NULL,
    media_url TEXT NOT NULL,
    CONSTRAINT fk_job_review_media_review FOREIGN KEY (job_review_id) REFERENCES job_reviews(id) ON DELETE CASCADE
);

-- changeset antigravity:50
CREATE INDEX IF NOT EXISTS idx_job_review_media_review_id ON job_review_media(job_review_id);
