package lk.helphub.api.admin.application.dto;

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
@Schema(description = "Request body for admin to update a job status")
public class AdminJobStatusUpdateRequest {

    @Schema(description = "New status for the job", example = "COMPLETED", allowableValues = {"OPEN", "IN_PROGRESS", "PENDING_CONFIRMATION", "COMPLETED", "CANCELLED", "DISPUTED"})
    @NotBlank(message = "Status is required")
    private String status;
}
