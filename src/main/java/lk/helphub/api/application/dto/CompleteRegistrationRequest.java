package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request to complete user registration after phone verification")
public class CompleteRegistrationRequest {
    @NotBlank(message = "Pending token is required")
    @Schema(description = "Short-lived token received from OTP verification step", example = "uuid-token-string")
    private String pendingToken;

    @NotBlank(message = "First name is required")
    @Schema(description = "User's first name", example = "John")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Schema(description = "User's date of birth", example = "1990-01-01")
    private LocalDate dateOfBirth;

    @Email(message = "Invalid email format")
    @Schema(description = "User's email address (optional)", example = "john.doe@example.com")
    private String email;
}
