package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lk.helphub.api.domain.enums.Gender;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Schema(description = "Comprehensive response payload representing a Provider Profile")
public class ProviderProfileResponse {

    private UUID id;
    private PersonalDetailsResponse personalDetails;
    private AddressDetailsResponse addressDetails;
    private String professionalBio;
    
    private List<ProviderIdentityDocumentResponse> identityDocuments;
    private List<ProviderSkillResponse> skills;
    private List<ProviderSkillProofResponse> skillProofs;
    private List<UserEducationResponse> academicQualifications;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    public static class PersonalDetailsResponse {
        private String fullName;
        private String displayName;
        private String email;
        private String phone;
        private LocalDate dob;
        private Gender gender;
        private String profilePictureUrl;
    }

    @Data
    @Builder
    public static class AddressDetailsResponse {
        private String streetAddress;
        private String city;
        private String province;
        private String zipCode;
        private String country;
        private BigDecimal latitude;
        private BigDecimal longitude;
    }
}
