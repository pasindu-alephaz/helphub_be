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
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request to verify a phone OTP")
public class PhoneOtpVerifyRequest {
    @NotBlank(message = "Phone number is required")
    @Schema(description = "User's phone number", example = "+94771234567", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phoneNumber;

    @NotBlank(message = "OTP is required")
    @Size(min = 6, max = 6, message = "OTP must be exactly 6 digits")
    @Schema(description = "The 6-digit OTP code", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String otp;

    @Schema(description = "Optional token from social login to link account", example = "uuid-token-string")
    private String pendingToken;
}
