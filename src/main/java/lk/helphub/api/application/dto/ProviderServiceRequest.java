package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request for adding a provider skill/service")
public class ProviderServiceRequest {

    @Schema(description = "Category ID", example = "550e8400-e29b-41d4-a716-446655440000")
    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    @Schema(description = "Subcategory ID (Optional)", example = "550e8400-e29b-41d4-a716-446655440001")
    private UUID subcategoryId;

    @Schema(description = "Skill level", example = "INTERMEDIATE")
    @NotNull(message = "Skill level is required")
    private String skillLevel; // BEGINNER, INTERMEDIATE, EXPERT

    @Schema(description = "Relationship/Specialization", example = "Residential Plumbing")
    private String relationship;
}
