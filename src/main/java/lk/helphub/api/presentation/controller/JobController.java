package lk.helphub.api.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.helphub.api.application.dto.JobCreateRequest;
import lk.helphub.api.application.dto.JobResponse;
import lk.helphub.api.application.dto.JobTemplateCreateRequest;
import lk.helphub.api.application.dto.JobTemplateUpdateRequest;
import lk.helphub.api.application.dto.JobFromTemplateRequest;
import lk.helphub.api.application.dto.JobTemplateResponse;
import lk.helphub.api.application.dto.JobUpdateRequest;
import lk.helphub.api.application.dto.ProviderCompleteRequest;
import lk.helphub.api.application.dto.DisputeJobRequest;
import lk.helphub.api.application.dto.CancelJobRequest;
import lk.helphub.api.application.dto.RejectJobRequest;
import lk.helphub.api.application.dto.ImageResponse;
import lk.helphub.api.application.services.JobService;
import lk.helphub.api.domain.enums.ResponseStatusCode;
import lk.helphub.api.presentation.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.UUID;
import java.util.List;
import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
@Tag(name = "Jobs", description = "Job Posting and Requests management APIs")
public class JobController {

    private final JobService jobService;

    @PostMapping
    @Operation(summary = "Create a new job request", description = "Creates a new job request for the currently authenticated user")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request body or validation errors",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"VALIDATION_ERROR\",\n  \"message\": \"Validation failed\",\n  \"errors\": {\n    \"field_name\": [\"error message\"]\n  }\n}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Valid JWT token required",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"UNAUTHORIZED\",\n  \"message\": \"Valid JWT token required\"\n}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category or Subcategory not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"NOT_FOUND\",\n  \"message\": \"Category or Subcategory not found\"\n}")))
    })
    @PreAuthorize("hasAuthority('job_create')")
    public ResponseEntity<ApiResponse<JobResponse>> createJob(
            Principal principal,
            @Valid @RequestBody JobCreateRequest request
    ) {
        JobResponse response = jobService.createJob(principal.getName(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<JobResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Job created successfully")
                .data(response)
                .build());
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload images for a job", description = "Upload multiple images for a specific job (max 5MB per image, JPG/PNG only)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Images uploaded successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid file size or format",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"BAD_REQUEST\",\n  \"message\": \"Invalid file size or format\"\n}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Valid JWT token required",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"UNAUTHORIZED\",\n  \"message\": \"Valid JWT token required\"\n}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Not authorized to upload for this job",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"FORBIDDEN\",\n  \"message\": \"Access Denied: You are not authorized to perform this operation\"\n}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Job not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"NOT_FOUND\",\n  \"message\": \"Job not found\"\n}")))
    })
    @PreAuthorize("hasAuthority('job_update')")
    public ResponseEntity<ApiResponse<Void>> uploadJobImages(
            Principal principal,
            @Parameter(description = "ID of the job to upload images for") @PathVariable UUID id,
            @Parameter(description = "Array of image files to upload") @RequestPart("images") MultipartFile[] images
    ) {
        jobService.uploadJobImages(id, principal.getName(), images);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Images uploaded successfully")
                .build());
    }

    @GetMapping("/{id}/images")
    @Operation(summary = "Get job images", description = "Retrieve all image URLs attached to a specific job")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Images retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Job not found")
    })
    @PreAuthorize("hasAuthority('job_read')")
    public ResponseEntity<ApiResponse<List<ImageResponse>>> getJobImages(
            @Parameter(description = "ID of the job") @PathVariable UUID id
    ) {
        List<ImageResponse> images = jobService.getJobImages(id);
        return ResponseEntity.ok(ApiResponse.<List<ImageResponse>>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Images retrieved successfully")
                .data(images)
                .build());
    }

    @DeleteMapping("/{id}/images/{imageId}")
    @Operation(summary = "Delete job image", description = "Delete a specific image from a job (restricted to job poster)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Image deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Not the job poster"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Job or Image not found")
    })
    @PreAuthorize("hasAuthority('job_update')")
    public ResponseEntity<ApiResponse<Void>> deleteJobImage(
            Principal principal,
            @Parameter(description = "ID of the job") @PathVariable UUID id,
            @Parameter(description = "ID of the image to delete") @PathVariable UUID imageId
    ) {
        jobService.deleteJobImage(id, imageId, principal.getName());
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Image deleted successfully")
                .build());
    }

    @PostMapping("/templates")
    @Operation(summary = "Create a new job template", description = "Save a job configuration as a reusable template for the user")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Job Template created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request body or validation errors",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"VALIDATION_ERROR\",\n  \"message\": \"Validation failed\",\n  \"errors\": {\n    \"field_name\": [\"error message\"]\n  }\n}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Valid JWT token required",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"UNAUTHORIZED\",\n  \"message\": \"Valid JWT token required\"\n}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category or Subcategory not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"NOT_FOUND\",\n  \"message\": \"Category or Subcategory not found\"\n}")))
    })
    @PreAuthorize("hasAuthority('job_template_create')")
    public ResponseEntity<ApiResponse<JobTemplateResponse>> createJobTemplate(
            Principal principal,
            @Valid @RequestBody JobTemplateCreateRequest request
    ) {
        JobTemplateResponse response = jobService.createJobTemplate(principal.getName(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<JobTemplateResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Job Template created successfully")
                .data(response)
                .build());
    }

    @GetMapping("/templates")
    @Operation(summary = "Get my job templates", description = "Retrieve all job templates created by the authenticated user")
    @PreAuthorize("hasAuthority('job_template_read')")
    public ResponseEntity<ApiResponse<List<JobTemplateResponse>>> getMyTemplates(Principal principal) {
        List<JobTemplateResponse> response = jobService.getMyTemplates(principal.getName());
        return ResponseEntity.ok(ApiResponse.<List<JobTemplateResponse>>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Templates retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/templates/{id}")
    @Operation(summary = "Get template by ID", description = "Retrieve a specific job template by its ID")
    @PreAuthorize("hasAuthority('job_template_read')")
    public ResponseEntity<ApiResponse<JobTemplateResponse>> getTemplateById(
            Principal principal,
            @PathVariable UUID id
    ) {
        JobTemplateResponse response = jobService.getTemplateById(id, principal.getName());
        return ResponseEntity.ok(ApiResponse.<JobTemplateResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Template retrieved successfully")
                .data(response)
                .build());
    }

    @PutMapping("/templates/{id}")
    @Operation(summary = "Update job template", description = "Update an existing job template")
    @PreAuthorize("hasAuthority('job_template_update')")
    public ResponseEntity<ApiResponse<JobTemplateResponse>> updateTemplate(
            Principal principal,
            @PathVariable UUID id,
            @Valid @RequestBody JobTemplateUpdateRequest request
    ) {
        JobTemplateResponse response = jobService.updateTemplate(id, principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.<JobTemplateResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Template updated successfully")
                .data(response)
                .build());
    }

    @DeleteMapping("/templates/{id}")
    @Operation(summary = "Delete job template", description = "Permanently delete a job template")
    @PreAuthorize("hasAuthority('job_template_delete')")
    public ResponseEntity<ApiResponse<Void>> deleteTemplate(
            Principal principal,
            @PathVariable UUID id
    ) {
        jobService.deleteTemplate(id, principal.getName());
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Template deleted successfully")
                .build());
    }

    @PostMapping("/templates/{id}/use")
    @Operation(summary = "Create job from template", description = "Create a new job using a saved template with optional overrides")
    @PreAuthorize("hasAuthority('job_create')")
    public ResponseEntity<ApiResponse<JobResponse>> createJobFromTemplate(
            Principal principal,
            @PathVariable UUID id,
            @RequestBody(required = false) JobFromTemplateRequest request
    ) {
        JobResponse response = jobService.createJobFromTemplate(id, principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<JobResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Job created from template successfully")
                .data(response)
                .build());
    }

    @GetMapping
    @Operation(summary = "List and filter jobs", description = "List jobs with pagination and optional filters")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Jobs retrieved successfully")
    })
    public ResponseEntity<ApiResponse<Page<JobResponse>>> getJobs(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            @Parameter(description = "Filter by subcategory UUID") @RequestParam(required = false) UUID subcategoryId,
            @Parameter(description = "Filter by status (OPEN, IN_PROGRESS, COMPLETED, CANCELLED)") @RequestParam(required = false) String status,
            @Parameter(description = "Filter by urgency (Normal, Urgent)") @RequestParam(required = false) String urgencyFlag,
            @Parameter(description = "Minimum price") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum price") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "Filter by location city (fuzzy search)") @RequestParam(required = false) String locationCity,
            @Parameter(description = "Filter by job type (FIXED, BIDDING)") @RequestParam(required = false) String jobType
    ) {
        Page<JobResponse> jobs = jobService.getJobs(pageable, subcategoryId, status, urgencyFlag, minPrice, maxPrice, locationCity, jobType);
        return ResponseEntity.ok(ApiResponse.<Page<JobResponse>>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Jobs retrieved successfully")
                .data(jobs)
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job details", description = "Retrieve full details of a specific job by its ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Job details retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Job not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"NOT_FOUND\",\n  \"message\": \"Job not found\"\n}")))
    })
    public ResponseEntity<ApiResponse<JobResponse>> getJobById(@Parameter(description = "ID of the job") @PathVariable UUID id) {
        JobResponse response = jobService.getJobById(id);
        return ResponseEntity.ok(ApiResponse.<JobResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Job retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/nearby")
    @Operation(summary = "Find nearby jobs", description = "Find jobs within a specific radius using geographic coordinates")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Nearby jobs retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid coordinate format",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"BAD_REQUEST\",\n  \"message\": \"Invalid coordinates format\"\n}")))
    })
    public ResponseEntity<ApiResponse<List<JobResponse>>> getNearbyJobs(
            @Parameter(description = "Coordinates in WKT POINT format (e.g. POINT(lon lat))") @RequestParam String coordinates,
            @Parameter(description = "Radius in kilometers to search within") @RequestParam(defaultValue = "10") double radiusKm,
            @Parameter(description = "Optional subcategory UUID filter") @RequestParam(required = false) UUID subcategoryId
    ) {
        List<JobResponse> jobs = jobService.getNearbyJobs(coordinates, radiusKm, subcategoryId);
        return ResponseEntity.ok(ApiResponse.<List<JobResponse>>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Nearby jobs retrieved successfully")
                .data(jobs)
                .build());
    }

    @GetMapping("/my-jobs")
    @Operation(summary = "Get my posted jobs", description = "Get all jobs posted by the authenticated user")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Jobs retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Valid JWT token required")
    })
    @PreAuthorize("hasAuthority('job_read')")
    public ResponseEntity<ApiResponse<Page<JobResponse>>> getMyPostedJobs(
            Principal principal,
            @PageableDefault(size = 20) Pageable pageable,
            @Parameter(description = "Filter by status (OPEN, IN_PROGRESS, COMPLETED, CANCELLED)") @RequestParam(required = false) String status
    ) {
        Page<JobResponse> jobs = jobService.getMyPostedJobs(principal.getName(), pageable, status);
        return ResponseEntity.ok(ApiResponse.<Page<JobResponse>>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("My posted jobs retrieved successfully")
                .data(jobs)
                .build());
    }

    @GetMapping("/accepted")
    @Operation(summary = "Get accepted jobs", description = "Get jobs accepted by the authenticated user (service provider view)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Jobs retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Valid JWT token required")
    })
    @PreAuthorize("hasAuthority('job_read')")
    public ResponseEntity<ApiResponse<Page<JobResponse>>> getAcceptedJobs(
            Principal principal,
            @PageableDefault(size = 20) Pageable pageable,
            @Parameter(description = "Filter by status (OPEN, IN_PROGRESS, COMPLETED, CANCELLED)") @RequestParam(required = false) String status
    ) {
        Page<JobResponse> jobs = jobService.getAcceptedJobs(principal.getName(), pageable, status);
        return ResponseEntity.ok(ApiResponse.<Page<JobResponse>>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Accepted jobs retrieved successfully")
                .data(jobs)
                .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update job", description = "Update job details (only by the job poster)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Job updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request or job not in OPEN status"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Not the job poster"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Job not found")
    })
    @PreAuthorize("hasAuthority('job_update')")
    public ResponseEntity<ApiResponse<JobResponse>> updateJob(
            Principal principal,
            @Parameter(description = "ID of the job") @PathVariable UUID id,
            @Valid @RequestBody JobUpdateRequest request
    ) {
        JobResponse response = jobService.updateJob(id, principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.<JobResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Job updated successfully")
                .data(response)
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete job", description = "Soft-delete/cancel a job (only by the job poster)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Job deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Not the job poster"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Job not found")
    })
    @PreAuthorize("hasAuthority('job_delete')")
    public ResponseEntity<ApiResponse<Void>> deleteJob(
            Principal principal,
            @Parameter(description = "ID of the job") @PathVariable UUID id
    ) {
        jobService.deleteJob(id, principal.getName());
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Job deleted successfully")
                .build());
    }

    @PostMapping("/{id}/accept")
    @Operation(summary = "Accept/claim a job", description = "Claim an open job as a service provider")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Job accepted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Job not open for acceptance or poster trying to accept own job")
    })
    @PreAuthorize("hasAuthority('job_accept')")
    public ResponseEntity<ApiResponse<JobResponse>> acceptJob(
            Principal principal,
            @PathVariable UUID id
    ) {
        JobResponse response = jobService.acceptJob(id, principal.getName());
        return ResponseEntity.ok(ApiResponse.<JobResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Job accepted successfully")
                .data(response)
                .build());
    }

    @PostMapping("/{id}/provider-complete")
    @Operation(summary = "Provider mark as complete", description = "Provider indicates they have finished the work")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Job marked as pending confirmation"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid state or unauthorized")
    })
    @PreAuthorize("hasAuthority('job_complete_provider')")
    public ResponseEntity<ApiResponse<JobResponse>> providerCompleteJob(
            Principal principal,
            @PathVariable UUID id,
            @RequestBody(required = false) ProviderCompleteRequest request
    ) {
        JobResponse response = jobService.providerCompleteJob(id, principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.<JobResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Job marked as pending confirmation")
                .data(response)
                .build());
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Confirm completion", description = "Job poster confirms the job is completed")
    @PreAuthorize("hasAuthority('job_complete')")
    public ResponseEntity<ApiResponse<JobResponse>> completeJob(
            Principal principal,
            @PathVariable UUID id
    ) {
        JobResponse response = jobService.completeJob(id, principal.getName());
        return ResponseEntity.ok(ApiResponse.<JobResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Job completed successfully")
                .data(response)
                .build());
    }

    @PostMapping("/{id}/dispute")
    @Operation(summary = "Initiate dispute", description = "Either poster or provider raises an issue")
    @PreAuthorize("hasAuthority('job_dispute')")
    public ResponseEntity<ApiResponse<JobResponse>> disputeJob(
            Principal principal,
            @PathVariable UUID id,
            @RequestBody DisputeJobRequest request
    ) {
        JobResponse response = jobService.disputeJob(id, principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.<JobResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Job status changed to DISPUTED")
                .data(response)
                .build());
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel job", description = "Job poster cancels the job")
    @PreAuthorize("hasAuthority('job_cancel')")
    public ResponseEntity<ApiResponse<JobResponse>> cancelJob(
            Principal principal,
            @PathVariable UUID id,
            @RequestBody(required = false) CancelJobRequest request
    ) {
        JobResponse response = jobService.cancelJob(id, principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.<JobResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Job cancelled successfully")
                .data(response)
                .build());
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "Start job", description = "Provider confirms work has begun")
    @PreAuthorize("hasAuthority('job_start')")
    public ResponseEntity<ApiResponse<JobResponse>> startJob(
            Principal principal,
            @PathVariable UUID id
    ) {
        JobResponse response = jobService.startJob(id, principal.getName());
        return ResponseEntity.ok(ApiResponse.<JobResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Job started successfully")
                .data(response)
                .build());
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject job", description = "Provider decides not to proceed")
    @PreAuthorize("hasAuthority('job_reject')")
    public ResponseEntity<ApiResponse<JobResponse>> rejectJob(
            Principal principal,
            @PathVariable UUID id,
            @RequestBody(required = false) RejectJobRequest request
    ) {
        JobResponse response = jobService.rejectJob(id, principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.<JobResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Job rejected and returned to OPEN status")
                .data(response)
                .build());
    }
}
