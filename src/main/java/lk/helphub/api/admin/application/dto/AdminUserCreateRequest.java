package lk.helphub.api.admin.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lk.helphub.api.domain.enums.IdentityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request object for admin to create a new user")
public class AdminUserCreateRequest {

    @Schema(description = "User's email address", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "User's password (min 8 characters, must contain uppercase, lowercase, number)", 
            example = "Password123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @Schema(description = "User's first name", example = "John")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @Schema(description = "User's phone number", example = "+94771234567")
    @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Invalid phone number format")
    private String phoneNumber;

    @Schema(description = "User's date of birth", example = "1990-01-01")
    private LocalDate dateOfBirth;

    @Schema(description = "Identity document type: NIC, PASSPORT, or LICENSE", example = "NIC")
    private IdentityType identityType;

    @Schema(description = "Identity document value corresponding to the selected type", example = "199012345678")
    @Size(max = 100, message = "Identity value must not exceed 100 characters")
    private String identityValue;

    @Schema(description = "App language preference: SINHALA, ENGLISH, or TAMIL", example = "SINHALA")
    private String languagePreference;

    @Schema(description = "Detailed bio or about me section")
    private String bio;

    @Schema(description = "Type of user account: customer, provider", example = "customer")
    private String userType;

    @Schema(description = "Status of the user account: active, inactive, suspended", example = "active")
    private String status;

    @Schema(description = "Set of role names to assign to the user", example = "[\"CUSTOMER\"]")
    private Set<String> roles;

    @Schema(description = "Whether to send welcome email to the user", example = "true")
    private boolean sendWelcomeEmail = true;
}
