package lk.helphub.api.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.helphub.api.application.dto.ReviewRequest;
import lk.helphub.api.application.dto.ReviewResponse;
import lk.helphub.api.application.services.ReviewService;
import lk.helphub.api.domain.enums.ResponseStatusCode;
import lk.helphub.api.presentation.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Job Reviews", description = "APIs for job reviews and ratings")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/api/v1/jobs/{id}/reviews/provider")
    @Operation(summary = "Submit provider review", description = "User rates the provider after job completion")
    @PreAuthorize("hasAuthority('job_review')")
    public ResponseEntity<ApiResponse<ReviewResponse>> submitProviderReview(
            Principal principal,
            @PathVariable UUID id,
            @Valid @RequestBody ReviewRequest request
    ) {
        ReviewResponse response = reviewService.submitProviderReview(id, principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<ReviewResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Provider review submitted successfully")
                .data(response)
                .build());
    }

    @PostMapping("/api/v1/jobs/{id}/reviews/user")
    @Operation(summary = "Submit user review", description = "Provider rates the user after job completion")
    @PreAuthorize("hasAuthority('job_review')")
    public ResponseEntity<ApiResponse<ReviewResponse>> submitUserReview(
            Principal principal,
            @PathVariable UUID id,
            @Valid @RequestBody ReviewRequest request
    ) {
        ReviewResponse response = reviewService.submitUserReview(id, principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<ReviewResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("User review submitted successfully")
                .data(response)
                .build());
    }

    @GetMapping("/api/v1/jobs/{id}/reviews")
    @Operation(summary = "Get job reviews", description = "Get all reviews for a job")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getJobReviews(
            @PathVariable UUID id
    ) {
        List<ReviewResponse> response = reviewService.getJobReviews(id);
        return ResponseEntity.ok(ApiResponse.<List<ReviewResponse>>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Job reviews retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/api/v1/providers/{id}/reviews")
    @Operation(summary = "Get provider reviews", description = "Get all reviews for a provider with pagination")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getProviderReviews(
            @PathVariable UUID id,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<ReviewResponse> response = reviewService.getProviderReviews(id, pageable);
        return ResponseEntity.ok(ApiResponse.<Page<ReviewResponse>>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Provider reviews retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/api/v1/users/{id}/reviews")
    @Operation(summary = "Get user reviews", description = "Get all reviews for a user with pagination")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getUserReviews(
            @PathVariable UUID id,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<ReviewResponse> response = reviewService.getUserReviews(id, pageable);
        return ResponseEntity.ok(ApiResponse.<Page<ReviewResponse>>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("User reviews retrieved successfully")
                .data(response)
                .build());
    }
}
