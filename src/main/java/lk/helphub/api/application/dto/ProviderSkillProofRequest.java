package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "Request payload for uploading a proof of skill")
public class ProviderSkillProofRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @Schema(description = "Optional description of the proof")
    private String description;

    @Schema(description = "Optional Subcategory ID to link this proof directly to a skill")
    private UUID subcategoryId;
}
