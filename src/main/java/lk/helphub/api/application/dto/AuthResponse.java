package lk.helphub.api.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response containing authentication token")
public class AuthResponse {
    @Schema(description = "JWT authentication token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Flag indicating if phone verification screen should be shown")
    private boolean phoneVerificationRequired;

    @Schema(description = "Flag indicating if registration screen should be shown")
    private boolean registrationRequired;

    @Schema(description = "Short-lived token linking current session to OTP verification")
    private String pendingToken;

    @Schema(description = "Flag indicating if 2FA verification is required (for admins)")
    private boolean twoFactorRequired;
}
