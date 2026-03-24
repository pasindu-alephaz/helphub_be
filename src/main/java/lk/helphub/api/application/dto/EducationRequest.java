package lk.helphub.api.application.dto;

public class EducationRequest {
    private String educationalLevel;

    public EducationRequest() {}

    public EducationRequest(String educationalLevel) {
        this.educationalLevel = educationalLevel;
    }

    public String getEducationalLevel() { return educationalLevel; }
    public void setEducationalLevel(String educationalLevel) { this.educationalLevel = educationalLevel; }

    public static EducationRequestBuilder builder() {
        return new EducationRequestBuilder();
    }

    public static class EducationRequestBuilder {
        private String educationalLevel;

        public EducationRequestBuilder educationalLevel(String educationalLevel) { this.educationalLevel = educationalLevel; return this; }

        public EducationRequest build() {
            return new EducationRequest(educationalLevel);
        }
    }
}
