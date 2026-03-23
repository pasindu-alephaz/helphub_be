package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobTemplateUpdateRequest {

    @Schema(description = "Name for this template to identify it later", example = "Updated House Cleaning")
    @Size(max = 100, message = "Template name cannot exceed 100 characters")
    private String templateName;

    @Schema(description = "Title of the job request", example = "Updated house cleaner needed")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;

    @Schema(description = "Detailed description of the job", example = "Updated detailed description.")
    @Size(min = 20, max = 500, message = "Description must be between 20 and 500 characters")
    private String description;

    @Schema(description = "Subcategory UUID")
    private UUID subcategoryId;

    @Schema(description = "Location address string", example = "456 Side St, Cityville")
    private String locationAddress;

    @Schema(description = "GPS coordinates in POINT format", example = "POINT(79.8612 6.9271)")
    private String locationCoordinates;

    @Schema(description = "Offered price for the job", example = "3500.00")
    private BigDecimal price;

    @Schema(description = "Urgency flag", example = "Urgent", allowableValues = {"Normal", "Urgent"})
    private String urgencyFlag;

    @Schema(description = "Job type (FIXED, BIDDING)", example = "BIDDING")
    private String jobType;

    @Schema(description = "Preferred price for the job", example = "3000.00")
    private BigDecimal preferredPrice;
}
