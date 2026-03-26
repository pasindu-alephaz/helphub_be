package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequest {

    @Schema(description = "Rating from 1 to 5 stars", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @Schema(description = "Review comment", example = "Great service! Very professional and completed the job on time.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 1000, message = "Comment must be at most 1000 characters")
    private String comment;

    @Schema(description = "List of media URLs (images/videos) attached to the review", example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> mediaUrls;
}
