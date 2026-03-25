package lk.helphub.api.application.dto;

import java.util.UUID;

public class UserEducationResponse {
    private UUID id;
    private String educationalLevel;
    private String certificateUrl;

    public UserEducationResponse() {}

    public UserEducationResponse(UUID id, String educationalLevel, String certificateUrl) {
        this.id = id;
        this.educationalLevel = educationalLevel;
        this.certificateUrl = certificateUrl;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getEducationalLevel() { return educationalLevel; }
    public void setEducationalLevel(String educationalLevel) { this.educationalLevel = educationalLevel; }

    public String getCertificateUrl() { return certificateUrl; }
    public void setCertificateUrl(String certificateUrl) { this.certificateUrl = certificateUrl; }

    public static UserEducationResponseBuilder builder() {
        return new UserEducationResponseBuilder();
    }

    public static class UserEducationResponseBuilder {
        private UUID id;
        private String educationalLevel;
        private String certificateUrl;

        public UserEducationResponseBuilder id(UUID id) { this.id = id; return this; }
        public UserEducationResponseBuilder educationalLevel(String educationalLevel) { this.educationalLevel = educationalLevel; return this; }
        public UserEducationResponseBuilder certificateUrl(String certificateUrl) { this.certificateUrl = certificateUrl; return this; }

        public UserEducationResponse build() {
            return new UserEducationResponse(id, educationalLevel, certificateUrl);
        }
    }
}
