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
@Schema(description = "Request to send verification OTP(s) for email and/or phone")
public class SendVerificationRequest {

    @Schema(description = "Email address to verify", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Phone number to verify (with country code)", example = "+94771234567")
    private String phoneNumber;
}
