package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing authentication tokens")
public class AuthResponse {
    @Schema(description = "Short-lived JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "Long-lived opaque refresh token used to obtain new access tokens")
    private String refreshToken;

    @Schema(description = "Flag indicating if phone verification screen should be shown")
    private boolean phoneVerificationRequired;

    @Schema(description = "Flag indicating if registration screen should be shown")
    private boolean registrationRequired;

    @Schema(description = "Short-lived token linking current session to OTP verification")
    private String pendingToken;

    @Schema(description = "Flag indicating if 2FA verification is required (for admins)")
    private boolean twoFactorRequired;

    public AuthResponse() {}

    public AuthResponse(String accessToken, String refreshToken, boolean phoneVerificationRequired, boolean registrationRequired, String pendingToken, boolean twoFactorRequired) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.phoneVerificationRequired = phoneVerificationRequired;
        this.registrationRequired = registrationRequired;
        this.pendingToken = pendingToken;
        this.twoFactorRequired = twoFactorRequired;
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public boolean isPhoneVerificationRequired() { return phoneVerificationRequired; }
    public void setPhoneVerificationRequired(boolean phoneVerificationRequired) { this.phoneVerificationRequired = phoneVerificationRequired; }

    public boolean isRegistrationRequired() { return registrationRequired; }
    public void setRegistrationRequired(boolean registrationRequired) { this.registrationRequired = registrationRequired; }

    public String getPendingToken() { return pendingToken; }
    public void setPendingToken(String pendingToken) { this.pendingToken = pendingToken; }

    public boolean isTwoFactorRequired() { return twoFactorRequired; }
    public void setTwoFactorRequired(boolean twoFactorRequired) { this.twoFactorRequired = twoFactorRequired; }

    public static AuthResponseBuilder builder() {
        return new AuthResponseBuilder();
    }

    public static class AuthResponseBuilder {
        private String accessToken;
        private String refreshToken;
        private boolean phoneVerificationRequired;
        private boolean registrationRequired;
        private String pendingToken;
        private boolean twoFactorRequired;

        public AuthResponseBuilder accessToken(String accessToken) { this.accessToken = accessToken; return this; }
        public AuthResponseBuilder refreshToken(String refreshToken) { this.refreshToken = refreshToken; return this; }
        public AuthResponseBuilder phoneVerificationRequired(boolean phoneVerificationRequired) { this.phoneVerificationRequired = phoneVerificationRequired; return this; }
        public AuthResponseBuilder registrationRequired(boolean registrationRequired) { this.registrationRequired = registrationRequired; return this; }
        public AuthResponseBuilder pendingToken(String pendingToken) { this.pendingToken = pendingToken; return this; }
        public AuthResponseBuilder twoFactorRequired(boolean twoFactorRequired) { this.twoFactorRequired = twoFactorRequired; return this; }

        public AuthResponse build() {
            return new AuthResponse(accessToken, refreshToken, phoneVerificationRequired, registrationRequired, pendingToken, twoFactorRequired);
        }
    }
}
