package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
public class JobTemplateCreateRequest {

    @Schema(description = "Name for this template to identify it later", example = "Regular House Cleaning", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Template name is required")
    @Size(max = 100, message = "Template name cannot exceed 100 characters")
    private String templateName;

    @Schema(description = "Title of the job request", example = "Need a house cleaner", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;

    @Schema(description = "Detailed description of the job", example = "Clean the whole house including 3 bedrooms and 2 bathrooms.", requiredMode = Schema.RequiredMode.REQUIRED)
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

    @Schema(description = "Offered price for the job (optional)", example = "3000.00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private BigDecimal price;

    @Schema(description = "Urgency flag", example = "Normal", allowableValues = {"Normal", "Urgent"}, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String urgencyFlag;

    @Schema(description = "Filter by job type (FIXED, BIDDING)", example = "FIXED", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String jobType;

    @Schema(description = "Preferred price for the job", example = "2500.00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private BigDecimal preferredPrice;
}
