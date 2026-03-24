package lk.helphub.api.application.dto;

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
@Schema(description = "Request to delete a user profile. OTP is sent to the user's registered phone number prior to calling this endpoint.")
public class DeleteProfileRequest {

    @NotBlank(message = "OTP is required for profile deletion")
    @Schema(description = "6-digit OTP sent to the user's registered phone number", example = "123456",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String otp;

    @NotBlank(message = "A reason for deletion is required")
    @Schema(description = "Reason the user is deleting their profile", example = "I no longer need this service",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String deleteReason;
}
