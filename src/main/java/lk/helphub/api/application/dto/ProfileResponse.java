package lk.helphub.api.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lk.helphub.api.domain.enums.IdentityType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "User profile response details")
public class ProfileResponse {

    @Schema(description = "Unique identifier of the user")
    private UUID id;

    @Schema(description = "User's full name", example = "John Doe")
    private String fullName;

    @Schema(description = "User's display name", example = "johndoe")
    private String displayName;

    @Schema(description = "User's email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "User's phone number", example = "+94771234567")
    private String phoneNumber;

    @Schema(description = "User's birthday", example = "1990-01-01")
    private LocalDate birthday;

    @Schema(description = "Identity document type: NIC, PASSPORT, or LICENSE", example = "NIC")
    private IdentityType identityType;

    @Schema(description = "Identity document value", example = "199012345678")
    private String identityValue;

    @Schema(description = "App language preference: SINHALA, ENGLISH, or TAMIL", example = "SINHALA")
    private String languagePreference;

    @Schema(description = "Detailed bio or about me section")
    private String about;

    @Schema(description = "Profile picture URL")
    private String profilePictureUrl;

    @Schema(description = "Identity verification document URL")
    private String identityVerificationUrl;

    @Schema(description = "Type of user account", example = "customer")
    private String userType;

    @Schema(description = "Status of the user account", example = "active")
    private String status;

    @Schema(description = "Timestamp when the profile was last verified")
    private LocalDateTime lastVerifiedAt;

    @Schema(description = "List of education details")
    private List<UserEducationResponse> educationList;

    @Schema(description = "Professional details")
    private UserProfessionalDetailResponse professionalDetail;

    @Schema(description = "List of saved addresses")
    private List<UserAddressResponse> addresses;

    @Schema(description = "Communication language preferences")
    private List<UserLanguageResponse> languages;

    @Schema(description = "Timestamp when the profile was created")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the profile was last updated")
    private LocalDateTime updatedAt;

    public ProfileResponse() {}

    public ProfileResponse(UUID id, String fullName, String displayName, String email, String phoneNumber, LocalDate birthday, IdentityType identityType, String identityValue, String languagePreference, String about, String profilePictureUrl, String identityVerificationUrl, String userType, String status, LocalDateTime lastVerifiedAt, List<UserEducationResponse> educationList, UserProfessionalDetailResponse professionalDetail, List<UserAddressResponse> addresses, List<UserLanguageResponse> languages, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.fullName = fullName;
        this.displayName = displayName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
        this.identityType = identityType;
        this.identityValue = identityValue;
        this.languagePreference = languagePreference;
        this.about = about;
        this.profilePictureUrl = profilePictureUrl;
        this.identityVerificationUrl = identityVerificationUrl;
        this.userType = userType;
        this.status = status;
        this.lastVerifiedAt = lastVerifiedAt;
        this.educationList = educationList;
        this.professionalDetail = professionalDetail;
        this.addresses = addresses;
        this.languages = languages;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }

    public IdentityType getIdentityType() { return identityType; }
    public void setIdentityType(IdentityType identityType) { this.identityType = identityType; }

    public String getIdentityValue() { return identityValue; }
    public void setIdentityValue(String identityValue) { this.identityValue = identityValue; }

    public String getLanguagePreference() { return languagePreference; }
    public void setLanguagePreference(String languagePreference) { this.languagePreference = languagePreference; }

    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public String getIdentityVerificationUrl() { return identityVerificationUrl; }
    public void setIdentityVerificationUrl(String identityVerificationUrl) { this.identityVerificationUrl = identityVerificationUrl; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getLastVerifiedAt() { return lastVerifiedAt; }
    public void setLastVerifiedAt(LocalDateTime lastVerifiedAt) { this.lastVerifiedAt = lastVerifiedAt; }

    public List<UserEducationResponse> getEducationList() { return educationList; }
    public void setEducationList(List<UserEducationResponse> educationList) { this.educationList = educationList; }

    public UserProfessionalDetailResponse getProfessionalDetail() { return professionalDetail; }
    public void setProfessionalDetail(UserProfessionalDetailResponse professionalDetail) { this.professionalDetail = professionalDetail; }

    public List<UserAddressResponse> getAddresses() { return addresses; }
    public void setAddresses(List<UserAddressResponse> addresses) { this.addresses = addresses; }

    public List<UserLanguageResponse> getLanguages() { return languages; }
    public void setLanguages(List<UserLanguageResponse> languages) { this.languages = languages; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static ProfileResponseBuilder builder() {
        return new ProfileResponseBuilder();
    }

    public static class ProfileResponseBuilder {
        private UUID id;
        private String fullName;
        private String displayName;
        private String email;
        private String phoneNumber;
        private LocalDate birthday;
        private IdentityType identityType;
        private String identityValue;
        private String languagePreference;
        private String about;
        private String profilePictureUrl;
        private String identityVerificationUrl;
        private String userType;
        private String status;
        private LocalDateTime lastVerifiedAt;
        private List<UserEducationResponse> educationList;
        private UserProfessionalDetailResponse professionalDetail;
        private List<UserAddressResponse> addresses;
        private List<UserLanguageResponse> languages;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public ProfileResponseBuilder id(UUID id) { this.id = id; return this; }
        public ProfileResponseBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public ProfileResponseBuilder displayName(String displayName) { this.displayName = displayName; return this; }
        public ProfileResponseBuilder email(String email) { this.email = email; return this; }
        public ProfileResponseBuilder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public ProfileResponseBuilder birthday(LocalDate birthday) { this.birthday = birthday; return this; }
        public ProfileResponseBuilder identityType(IdentityType identityType) { this.identityType = identityType; return this; }
        public ProfileResponseBuilder identityValue(String identityValue) { this.identityValue = identityValue; return this; }
        public ProfileResponseBuilder languagePreference(String languagePreference) { this.languagePreference = languagePreference; return this; }
        public ProfileResponseBuilder about(String about) { this.about = about; return this; }
        public ProfileResponseBuilder profilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; return this; }
        public ProfileResponseBuilder identityVerificationUrl(String identityVerificationUrl) { this.identityVerificationUrl = identityVerificationUrl; return this; }
        public ProfileResponseBuilder userType(String userType) { this.userType = userType; return this; }
        public ProfileResponseBuilder status(String status) { this.status = status; return this; }
        public ProfileResponseBuilder lastVerifiedAt(LocalDateTime lastVerifiedAt) { this.lastVerifiedAt = lastVerifiedAt; return this; }
        public ProfileResponseBuilder educationList(List<UserEducationResponse> educationList) { this.educationList = educationList; return this; }
        public ProfileResponseBuilder professionalDetail(UserProfessionalDetailResponse professionalDetail) { this.professionalDetail = professionalDetail; return this; }
        public ProfileResponseBuilder addresses(List<UserAddressResponse> addresses) { this.addresses = addresses; return this; }
        public ProfileResponseBuilder languages(List<UserLanguageResponse> languages) { this.languages = languages; return this; }
        public ProfileResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ProfileResponseBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public ProfileResponse build() {
            return new ProfileResponse(id, fullName, displayName, email, phoneNumber, birthday, identityType, identityValue, languagePreference, about, profilePictureUrl, identityVerificationUrl, userType, status, lastVerifiedAt, educationList, professionalDetail, addresses, languages, createdAt, updatedAt);
        }
    }
}
