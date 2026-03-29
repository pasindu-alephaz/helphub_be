package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "Request payload for assigning a skill to a provider")
public class ProviderSkillRequest {

    @NotNull(message = "Subcategory ID is required")
    private UUID subcategoryId;

    @NotBlank(message = "Skill level is required")
    @Schema(description = "Level of expertise (e.g., NOVICE, ADVANCED_BEGINNER, COMPETENT, PROFICIENT, EXPERT)")
    private String skillLevel;
}
