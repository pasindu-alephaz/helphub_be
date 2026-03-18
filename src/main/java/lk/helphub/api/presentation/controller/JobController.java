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
import lk.helphub.api.application.dto.JobTemplateResponse;
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
    @PreAuthorize("isAuthenticated()")
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
    @PreAuthorize("isAuthenticated()")
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
    @PreAuthorize("isAuthenticated()")
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
}
