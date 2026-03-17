package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for Social Login authentication")
public class SocialAuthRequest {

    @NotBlank(message = "Token is required")
    @Schema(description = "The identity token received from the social provider", example = "eyJhbGc...")
    private String token;

    @Schema(description = "First name of the user (Optional, usually required for Apple's first login)", example = "John")
    private String firstName;

    @Schema(description = "Last name of the user (Optional, usually required for Apple's first login)", example = "Doe")
    private String lastName;
}
