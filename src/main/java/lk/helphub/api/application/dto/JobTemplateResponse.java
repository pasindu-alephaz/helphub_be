package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class JobTemplateResponse {

    @Schema(description = "Unique identifier of the job template", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID id;

    @Schema(description = "Name for this template to identify it later", example = "Regular House Cleaning")
    private String templateName;

    @Schema(description = "Title of the job request", example = "Need a house cleaner")
    private String title;

    @Schema(description = "Detailed description of the job", example = "Clean the whole house including 3 bedrooms and 2 bathrooms.")
    private String description;

    @Schema(description = "Subcategory UUID mapped to this template")
    private UUID subcategoryId;

    @Schema(description = "Location address string", example = "123 Main St, Cityville")
    private String locationAddress;

    @Schema(description = "GPS coordinates in POINT format", example = "POINT(79.8612 6.9271)")
    private String locationCoordinates;

    @Schema(description = "Offered price for the job")
    private BigDecimal price;

    @Schema(description = "Urgency flag", example = "Normal")
    private String urgencyFlag;

    @Schema(description = "User ID who created the template")
    private UUID userId;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}
