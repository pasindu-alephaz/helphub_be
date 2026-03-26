package lk.helphub.api.admin.application.services;

import lk.helphub.api.admin.application.dto.CategoryStatsResponse;
import lk.helphub.api.application.dto.JobResponse;
import lk.helphub.api.admin.application.dto.JobStatsResponse;
import lk.helphub.api.application.dto.JobUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AdminJobService {

    /**
     * List all jobs with extended filters (admin view)
     */
    Page<JobResponse> getAllJobs(
            Pageable pageable,
            UUID userId,
            UUID providerId,
            String status,
            UUID subcategoryId,
            LocalDateTime fromDate,
            LocalDateTime toDate
    );

    /**
     * Get detailed job information (admin view)
     */
    JobResponse getJobById(UUID id);

    /**
     * Update any job details (admin override)
     */
    JobResponse updateJob(UUID id, JobUpdateRequest request);

    /**
     * Permanently delete a job (admin soft delete)
     */
    void deleteJob(UUID id);

    /**
     * Force update job status (admin override)
     */
    JobResponse updateJobStatus(UUID id, String status);

    /**
     * List all reported or flagged jobs
     */
    Page<JobResponse> getReportedJobs(Pageable pageable);

    /**
     * Flag a job for review
     */
    JobResponse flagJob(UUID id, String reason);

    /**
     * Remove flag from a job
     */
    JobResponse unflagJob(UUID id);

    /**
     * Archive a completed job
     */
    JobResponse archiveJob(UUID id);

    /**
     * Get job statistics and counts
     */
    JobStatsResponse getJobStats(LocalDateTime fromDate, LocalDateTime toDate);

    /**
     * Get most requested job categories
     */
    List<CategoryStatsResponse> getPopularCategories(int limit);
}
