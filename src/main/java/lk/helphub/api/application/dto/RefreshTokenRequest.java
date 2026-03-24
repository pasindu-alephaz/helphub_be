package lk.helphub.api.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Request to obtain a new access token using a refresh token")
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token is required")
    @Schema(description = "The refresh token issued at login", required = true)
    private String refreshToken;
}
