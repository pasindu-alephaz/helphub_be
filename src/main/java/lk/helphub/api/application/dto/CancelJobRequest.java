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
@Schema(description = "Request body for cancelling a job")
public class CancelJobRequest {

    @Schema(description = "Reason for cancellation", example = "No longer need the service.")
    private String reason;
}
