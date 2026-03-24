package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Request to complete user registration after phone verification")
public class CompleteRegistrationRequest {
    @NotBlank(message = "Pending token is required")
    @Schema(description = "Short-lived token received from OTP verification step", example = "uuid-token-string")
    private String pendingToken;

    @NotBlank(message = "Full name is required")
    @Schema(description = "User's full name", example = "John Doe")
    private String fullName;
    
    @NotBlank(message = "Display name is required")
    @Schema(description = "User's display name", example = "johndoe")
    private String displayName;

    @NotNull(message = "Date of birth is required")
    @Schema(description = "User's date of birth", example = "1990-01-01")
    private LocalDate dateOfBirth;

    @Email(message = "Invalid email format")
    @Schema(description = "User's email address (optional)", example = "john.doe@example.com")
    private String email;

    public CompleteRegistrationRequest() {}

    public CompleteRegistrationRequest(String pendingToken, String fullName, String displayName, LocalDate dateOfBirth, String email) {
        this.pendingToken = pendingToken;
        this.fullName = fullName;
        this.displayName = displayName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
    }

    public String getPendingToken() { return pendingToken; }
    public void setPendingToken(String pendingToken) { this.pendingToken = pendingToken; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public static CompleteRegistrationRequestBuilder builder() {
        return new CompleteRegistrationRequestBuilder();
    }

    public static class CompleteRegistrationRequestBuilder {
        private String pendingToken;
        private String fullName;
        private String displayName;
        private LocalDate dateOfBirth;
        private String email;

        public CompleteRegistrationRequestBuilder pendingToken(String pendingToken) { this.pendingToken = pendingToken; return this; }
        public CompleteRegistrationRequestBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public CompleteRegistrationRequestBuilder displayName(String displayName) { this.displayName = displayName; return this; }
        public CompleteRegistrationRequestBuilder dateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; return this; }
        public CompleteRegistrationRequestBuilder email(String email) { this.email = email; return this; }

        public CompleteRegistrationRequest build() {
            return new CompleteRegistrationRequest(pendingToken, fullName, displayName, dateOfBirth, email);
        }
    }
}
