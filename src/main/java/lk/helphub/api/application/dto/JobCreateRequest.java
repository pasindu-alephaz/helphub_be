package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobCreateRequest {

    @Schema(description = "Title of the job request", example = "Need a plumber for a leaky pipe", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;

    @Schema(description = "Detailed description of the job", example = "The pipe under the kitchen sink is leaking heavily.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Description is required")
    @Size(min = 20, max = 500, message = "Description must be between 20 and 500 characters")
    private String description;

    @Schema(description = "Subcategory UUID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID subcategoryId;

    @Schema(description = "Location address string", example = "123 Main St, Cityville", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Location address is required")
    private String locationAddress;

    @Schema(description = "GPS coordinates in POINT format", example = "POINT(79.8612 6.9271)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Location coordinates are required")
    private String locationCoordinates;

    @Schema(description = "Offered price for the job (optional)", example = "1500.00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private BigDecimal price;

    @Schema(description = "Scheduled date and time", example = "2024-12-01T14:30:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime scheduledAt;

    @Schema(description = "Urgency flag", example = "Urgent", allowableValues = {"Normal", "Urgent"}, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String urgencyFlag;

}
