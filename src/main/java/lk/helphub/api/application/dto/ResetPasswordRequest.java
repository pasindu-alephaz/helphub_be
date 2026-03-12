package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
    @Schema(description = "OTP received in email", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "OTP is required")
    private String otp;

    @Schema(description = "New password for the account", example = "SecureP@ssw0rd123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String newPassword;
}
