package lk.helphub.api.admin.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request object for admin to delete a user")
public class AdminUserDeleteRequest {

    @Schema(description = "Reason for deleting the user account", 
            example = "User violated terms of service", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Delete reason is required")
    private String deleteReason;

    @Schema(description = "Whether to permanently delete the user (true) or soft delete (false)", 
            example = "false")
    private boolean permanent = false;

    @Schema(description = "Notify the user about account deletion via email")
    private boolean notifyUser = true;
}
