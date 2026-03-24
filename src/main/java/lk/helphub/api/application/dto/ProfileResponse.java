package lk.helphub.api.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lk.helphub.api.domain.enums.IdentityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "User profile response details")
public class ProfileResponse {

    @Schema(description = "Unique identifier of the user")
    private UUID id;

    @Schema(description = "User's first name", example = "John")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @Schema(description = "User's email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "User's phone number", example = "+94771234567")
    private String phoneNumber;

    @Schema(description = "User's date of birth", example = "1990-01-01")
    private LocalDate dateOfBirth;

    @Schema(description = "Identity document type: NIC, PASSPORT, or LICENSE", example = "NIC")
    private IdentityType identityType;

    @Schema(description = "Identity document value", example = "199012345678")
    private String identityValue;

    @Schema(description = "App language preference: SINHALA, ENGLISH, or TAMIL", example = "SINHALA")
    private String languagePreference;

    @Schema(description = "Detailed bio or about me section")
    private String bio;

    @Schema(description = "Profile picture URL")
    private String profileImageUrl;

    @Schema(description = "Type of user account", example = "customer")
    private String userType;

    @Schema(description = "Status of the user account", example = "active")
    private String status;

    @Schema(description = "List of saved addresses")
    private List<UserAddressResponse> addresses;

    @Schema(description = "Communication language preferences")
    private List<UserLanguageResponse> languages;

    @Schema(description = "Timestamp when the profile was created")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the profile was last updated")
    private LocalDateTime updatedAt;
}
