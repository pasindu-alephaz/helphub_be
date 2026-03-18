package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lk.helphub.api.domain.enums.IdentityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request object for updating user profile")
public class UpdateProfileRequest {

    @Schema(description = "User's first name", example = "John")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @Schema(description = "User's phone number", example = "+94771234567")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;

    @Schema(description = "Detailed bio or about me section", example = "Software Engineer with 5 years of experience.")
    private String bio;

    @Schema(description = "User's date of birth", example = "1990-01-01")
    private LocalDate dateOfBirth;

    @Schema(description = "Identity document type: NIC, PASSPORT, or LICENSE",
            example = "NIC", allowableValues = {"NIC", "PASSPORT", "LICENSE"})
    private IdentityType identityType;

    @Schema(description = "Identity document value corresponding to the selected type",
            example = "199012345678")
    @Size(max = 100, message = "Identity value must not exceed 100 characters")
    private String identityValue;

    @Schema(description = "App language preference: SINHALA, ENGLISH, or TAMIL",
            example = "SINHALA", allowableValues = {"SINHALA", "ENGLISH", "TAMIL"})
    private String languagePreference;
}
