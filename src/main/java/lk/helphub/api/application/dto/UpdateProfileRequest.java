package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lk.helphub.api.domain.enums.IdentityType;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Request object for updating user profile")
public class UpdateProfileRequest {

    @Schema(description = "User's full name", example = "John Doe")
    @Size(max = 200, message = "Full name must not exceed 200 characters")
    private String fullName;

    @Schema(description = "User's display name", example = "johndoe")
    @Size(max = 255, message = "Display name must not exceed 255 characters")
    private String displayName;

    @Schema(description = "User's email address", example = "john.doe@example.com")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format")
    private String email;

    @Schema(description = "User's phone number", example = "+94771234567")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;

    @Schema(description = "Detailed bio or about me section", example = "Software Engineer with 5 years of experience.")
    private String about;

    @Schema(description = "User's birthday", example = "1990-01-01")
    private LocalDate birthday;

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

    private List<EducationRequest> educationList;
    private ProfessionalDetailRequest professionalDetail;

    public UpdateProfileRequest() {}

    public UpdateProfileRequest(String fullName, String displayName, String email, String phoneNumber, String about, LocalDate birthday, IdentityType identityType, String identityValue, String languagePreference, List<EducationRequest> educationList, ProfessionalDetailRequest professionalDetail) {
        this.fullName = fullName;
        this.displayName = displayName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.about = about;
        this.birthday = birthday;
        this.identityType = identityType;
        this.identityValue = identityValue;
        this.languagePreference = languagePreference;
        this.educationList = educationList;
        this.professionalDetail = professionalDetail;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }

    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }

    public IdentityType getIdentityType() { return identityType; }
    public void setIdentityType(IdentityType identityType) { this.identityType = identityType; }

    public String getIdentityValue() { return identityValue; }
    public void setIdentityValue(String identityValue) { this.identityValue = identityValue; }

    public String getLanguagePreference() { return languagePreference; }
    public void setLanguagePreference(String languagePreference) { this.languagePreference = languagePreference; }

    public List<EducationRequest> getEducationList() { return educationList; }
    public void setEducationList(List<EducationRequest> educationList) { this.educationList = educationList; }

    public ProfessionalDetailRequest getProfessionalDetail() { return professionalDetail; }
    public void setProfessionalDetail(ProfessionalDetailRequest professionalDetail) { this.professionalDetail = professionalDetail; }

    public static UpdateProfileRequestBuilder builder() {
        return new UpdateProfileRequestBuilder();
    }

    public static class UpdateProfileRequestBuilder {
        private String fullName;
        private String displayName;
        private String email;
        private String phoneNumber;
        private String about;
        private LocalDate birthday;
        private IdentityType identityType;
        private String identityValue;
        private String languagePreference;
        private List<EducationRequest> educationList;
        private ProfessionalDetailRequest professionalDetail;

        public UpdateProfileRequestBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public UpdateProfileRequestBuilder displayName(String displayName) { this.displayName = displayName; return this; }
        public UpdateProfileRequestBuilder email(String email) { this.email = email; return this; }
        public UpdateProfileRequestBuilder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public UpdateProfileRequestBuilder about(String about) { this.about = about; return this; }
        public UpdateProfileRequestBuilder birthday(LocalDate birthday) { this.birthday = birthday; return this; }
        public UpdateProfileRequestBuilder identityType(IdentityType identityType) { this.identityType = identityType; return this; }
        public UpdateProfileRequestBuilder identityValue(String identityValue) { this.identityValue = identityValue; return this; }
        public UpdateProfileRequestBuilder languagePreference(String languagePreference) { this.languagePreference = languagePreference; return this; }
        public UpdateProfileRequestBuilder educationList(List<EducationRequest> educationList) { this.educationList = educationList; return this; }
        public UpdateProfileRequestBuilder professionalDetail(ProfessionalDetailRequest professionalDetail) { this.professionalDetail = professionalDetail; return this; }

        public UpdateProfileRequest build() {
            return new UpdateProfileRequest(fullName, displayName, email, phoneNumber, about, birthday, identityType, identityValue, languagePreference, educationList, professionalDetail);
        }
    }
}
