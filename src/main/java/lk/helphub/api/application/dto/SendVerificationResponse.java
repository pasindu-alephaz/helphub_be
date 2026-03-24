package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response containing verification token(s) for OTP validation")
public class SendVerificationResponse {

    @Schema(description = "Token for email OTP verification", example = "a1b2c3d4e5f6...")
    private String emailToken;

    @Schema(description = "Token for phone OTP verification", example = "f6e5d4c3b2a1...")
    private String phoneToken;
}
