package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body for initiating a job dispute")
public class DisputeJobRequest {

    @NotBlank(message = "Dispute reason is required")
    @Schema(description = "Reason for the dispute", example = "Provider did not show up on time.")
    private String reason;

    @Schema(description = "Evidence or additional details for the dispute", example = "Photos of the incomplete work attached.")
    private String evidence;
}
