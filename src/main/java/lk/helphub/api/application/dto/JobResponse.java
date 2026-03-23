package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobResponse {

    @Schema(description = "Unique identifier of the job", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID id;

    @Schema(description = "Title of the job request", example = "Need a plumber for a leaky pipe")
    private String title;

    @Schema(description = "Detailed description of the job", example = "The pipe under the kitchen sink is leaking heavily.")
    private String description;

    @Schema(description = "Subcategory UUID mapped to this job")
    private UUID subcategoryId;

    @Schema(description = "Location address string", example = "123 Main St, Cityville")
    private String locationAddress;

    @Schema(description = "GPS coordinates in POINT format", example = "POINT(79.8612 6.9271)")
    private String locationCoordinates;

    @Schema(description = "Offered price for the job")
    private BigDecimal price;

    @Schema(description = "Scheduled date and time", example = "2024-12-01T14:30:00")
    private LocalDateTime scheduledAt;

    @Schema(description = "Type of job: FIXED or BIDDING")
    private String jobType;

    @Schema(description = "Preferred price for the job")
    private BigDecimal preferredPrice;

    @Schema(description = "Urgency flag", example = "Urgent")
    private String urgencyFlag;

    @Schema(description = "Current status of the job", example = "OPEN")
    private String status;

    @Schema(description = "User ID who posted the job")
    private UUID postedBy;

    @Schema(description = "User ID who accepted the job")
    private UUID acceptedBy;

    @Schema(description = "List of image URLs attached to the job")
    private List<String> imageUrls;

    @Schema(description = "List of image details (ID and URL) attached to the job")
    private List<ImageResponse> images;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}
