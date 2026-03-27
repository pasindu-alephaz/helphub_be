package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lk.helphub.api.domain.enums.Gender;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request for provider registration and onboarding")
public class ProviderRegistrationRequest {

    // Personal Details
    @Schema(description = "Full name of the provider", example = "John Doe")
    @NotBlank(message = "Full name is required")
    private String fullName;

    @Schema(description = "Display name used on the platform", example = "John's Services")
    @NotBlank(message = "Display name is required")
    private String displayName;

    @Schema(description = "Email address", example = "john@example.com")
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(description = "Phone number", example = "+94771234567")
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @Schema(description = "Date of birth", example = "1990-01-01")
    @NotNull(message = "Date of birth is required")
    private LocalDate birthday;

    @Schema(description = "Gender", example = "MALE")
    @NotNull(message = "Gender is required")
    private Gender gender;

    // Professional Bio
    @Schema(description = "Professional bio or 'About me' section", example = "Experienced plumber with 10 years of experience.")
    private String about;

    // Profile Picture
    @Schema(description = "URL of the profile picture", example = "https://example.com/images/profile.jpg")
    private String profilePictureUrl;

    // Address Details (Multiple)
    @Schema(description = "List of addresses")
    private List<ProviderAddressRequest> addresses;

    // Academic Qualification (Multiple)
    @Schema(description = "List of academic qualifications")
    private List<AcademicQualificationRequest> qualifications;

    // Identity and Biometric
    @Schema(description = "Identity verification documents")
    private List<ProviderIdentityRequest> identityDocuments;

    // Provider Skills
    @Schema(description = "List of skills (service categories)")
    private List<ProviderServiceRequest> skills;

    // Proof of Skills / Portfolio
    @Schema(description = "Portfolio items with proof of skills")
    private List<ProviderPortfolioRequest> portfolioItems;

    @Schema(description = "Indicates if this is a draft submission", example = "false")
    private boolean isDraft;
}
