package lk.helphub.api.admin.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.helphub.api.admin.application.services.AdminJobService;
import lk.helphub.api.admin.application.dto.*;
import lk.helphub.api.application.dto.*;
import lk.helphub.api.domain.enums.ResponseStatusCode;
import lk.helphub.api.presentation.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/jobs")
@RequiredArgsConstructor
@Tag(name = "Admin - Job Management", description = "APIs for admin to manage jobs")
@SecurityRequirement(name = "bearerAuth")
public class AdminJobController {

    private final AdminJobService adminJobService;

    @GetMapping
    @Operation(summary = "List all jobs", description = "Retrieves all jobs with extended filters (admin view)")
    @PreAuthorize("hasAuthority('job_read')")
    public ResponseEntity<ApiResponse<Page<JobResponse>>> getAllJobs(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String sortDir,
            @Parameter(description = "Filter by poster UUID") @RequestParam(required = false) UUID userId,
            @Parameter(description = "Filter by provider UUID") @RequestParam(required = false) UUID providerId,
            @Parameter(description = "Filter by status") @RequestParam(required = false) String status,
            @Parameter(description = "Filter by subcategory UUID") @RequestParam(required = false) UUID subcategoryId,
            @Parameter(description = "Filter from date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @Parameter(description = "Filter to date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate
    ) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<JobResponse> jobs = adminJobService.getAllJobs(pageable, userId, providerId, status, subcategoryId, fromDate, toDate);

        return ResponseEntity.ok(ApiResponse.<Page<JobResponse>>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Jobs retrieved successfully")
                .data(jobs)
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed job information", description = "Retrieves full details for a specific job (admin view)")
    @PreAuthorize("hasAuthority('job_read')")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Job retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Job not found")
    })
    public ResponseEntity<ApiResponse<JobResponse>> getJobById(
            @Parameter(description = "Job ID") @PathVariable UUID id
    ) {
        JobResponse job = adminJobService.getJobById(id);
        return ResponseEntity.ok(ApiResponse.<JobResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Job retrieved successfully")
                .data(job)
                .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update any job details", description = "Admin override to update any job field")
    @PreAuthorize("hasAuthority('job_update')")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Job updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Job not found")
    })
    public ResponseEntity<ApiResponse<JobResponse>> updateJob(
            @Parameter(description = "Job ID") @PathVariable UUID id,
            @Valid @RequestBody JobUpdateRequest request
    ) {
        JobResponse job = adminJobService.updateJob(id, request);
        return ResponseEntity.ok(ApiResponse.<JobResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Job updated successfully")
                .data(job)
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a job", description = "Admin soft delete/cancellation of a job")
    @PreAuthorize("hasAuthority('job_delete')")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Job deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Job not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteJob(
            @Parameter(description = "Job ID") @PathVariable UUID id
    ) {
        adminJobService.deleteJob(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Job deleted successfully")
                .build());
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Force update job status", description = "Admin override to force change a job's status")
    @PreAuthorize("hasAuthority('job_update')")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Job status updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Job not found")
    })
    public ResponseEntity<ApiResponse<JobResponse>> updateJobStatus(
            @Parameter(description = "Job ID") @PathVariable UUID id,
            @Valid @RequestBody AdminJobStatusUpdateRequest request
    ) {
        JobResponse job = adminJobService.updateJobStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.<JobResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Job status updated successfully")
                .data(job)
                .build());
    }

    @GetMapping("/reports")
    @Operation(summary = "Get reported jobs", description = "Retrieves all flagged or reported jobs (admin view)")
    @PreAuthorize("hasAuthority('job_read')")
    public ResponseEntity<ApiResponse<Page<JobResponse>>> getReportedJobs(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        Page<JobResponse> jobs = adminJobService.getReportedJobs(pageable);

        return ResponseEntity.ok(ApiResponse.<Page<JobResponse>>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Reported jobs retrieved successfully")
                .data(jobs)
                .build());
    }

    @PostMapping("/{id}/flag")
    @Operation(summary = "Flag a job", description = "Marks a job for admin review with a reason")
    @PreAuthorize("hasAuthority('job_update')")
    public ResponseEntity<ApiResponse<JobResponse>> flagJob(
            @Parameter(description = "Job ID") @PathVariable UUID id,
            @Valid @RequestBody FlagJobRequest request
    ) {
        JobResponse job = adminJobService.flagJob(id, request.getReason());
        return ResponseEntity.ok(ApiResponse.<JobResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Job flagged successfully")
                .data(job)
                .build());
    }

    @PostMapping("/{id}/unflag")
    @Operation(summary = "Unflag a job", description = "Removes the review flag from a job")
    @PreAuthorize("hasAuthority('job_update')")
    public ResponseEntity<ApiResponse<JobResponse>> unflagJob(
            @Parameter(description = "Job ID") @PathVariable UUID id
    ) {
        JobResponse job = adminJobService.unflagJob(id);
        return ResponseEntity.ok(ApiResponse.<JobResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Job unflagged successfully")
                .data(job)
                .build());
    }

    @PostMapping("/{id}/archive")
    @Operation(summary = "Archive a job", description = "Moves a completed job to archived status")
    @PreAuthorize("hasAuthority('job_update')")
    public ResponseEntity<ApiResponse<JobResponse>> archiveJob(
            @Parameter(description = "Job ID") @PathVariable UUID id
    ) {
        JobResponse job = adminJobService.archiveJob(id);
        return ResponseEntity.ok(ApiResponse.<JobResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Job archived successfully")
                .data(job)
                .build());
    }

    @GetMapping("/stats")
    @Operation(summary = "Get job statistics", description = "Retrieves counts and averages for jobs within a date range")
    @PreAuthorize("hasAuthority('admin_job_stats_read')")
    public ResponseEntity<ApiResponse<JobStatsResponse>> getJobStats(
            @Parameter(description = "From date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @Parameter(description = "To date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate
    ) {
        JobStatsResponse stats = adminJobService.getJobStats(fromDate, toDate);
        return ResponseEntity.ok(ApiResponse.<JobStatsResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Job statistics retrieved successfully")
                .data(stats)
                .build());
    }

    @GetMapping("/analytics/popular-categories")
    @Operation(summary = "Get popular categories", description = "Retrieves the most requested job categories")
    @PreAuthorize("hasAuthority('admin_job_analytics_read')")
    public ResponseEntity<ApiResponse<List<CategoryStatsResponse>>> getPopularCategories(
            @Parameter(description = "Limit result set size") @RequestParam(defaultValue = "10") int limit
    ) {
        List<CategoryStatsResponse> categories = adminJobService.getPopularCategories(limit);
        return ResponseEntity.ok(ApiResponse.<List<CategoryStatsResponse>>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Popular categories retrieved successfully")
                .data(categories)
                .build());
    }
}
