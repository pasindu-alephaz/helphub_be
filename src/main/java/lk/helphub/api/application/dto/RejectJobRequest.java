package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body for rejecting an accepted job by provider")
public class RejectJobRequest {

    @Schema(description = "Reason for rejection", example = "Emergency came up, cannot fulfill the job.")
    private String reason;
}
