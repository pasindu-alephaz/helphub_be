package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {

    @Schema(description = "Unique identifier of the review", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "ID of the job being reviewed", example = "123e4567-e89b-12d3-a456-426614174001")
    private UUID jobId;

    @Schema(description = "Job title for reference", example = "Home cleaning service")
    private String jobTitle;

    @Schema(description = "ID of the reviewer (person who wrote the review)", example = "123e4567-e89b-12d3-a456-426614174002")
    private UUID reviewerId;

    @Schema(description = "Name of the reviewer", example = "John Doe")
    private String reviewerName;

    @Schema(description = "Profile image URL of the reviewer", example = "https://example.com/profile/john.jpg")
    private String reviewerProfileImage;

    @Schema(description = "ID of the reviewed user (person being reviewed)", example = "123e4567-e89b-12d3-a456-426614174003")
    private UUID reviewedUserId;

    @Schema(description = "Name of the reviewed user", example = "Jane Smith")
    private String reviewedUserName;

    @Schema(description = "Profile image URL of the reviewed user", example = "https://example.com/profile/jane.jpg")
    private String reviewedUserProfileImage;

    @Schema(description = "Rating from 1 to 5 stars", example = "5")
    private Integer rating;

    @Schema(description = "Review comment", example = "Great service! Very professional and completed the job on time.")
    private String comment;

    @Schema(description = "List of media URLs attached to the review", example = "[\"https://example.com/image1.jpg\"]")
    private List<String> mediaUrls;

    @Schema(description = "Type of review - PROVIDER (user reviewing provider) or USER (provider reviewing user)", example = "PROVIDER")
    private String reviewType;

    @Schema(description = "Timestamp when the review was created")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the review was last updated")
    private LocalDateTime updatedAt;
}
