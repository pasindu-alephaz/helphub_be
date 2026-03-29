package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for professional bio")
public class ProfessionalBioRequest {
    @Schema(description = "Long-form professional bio")
    private String bio;
}
