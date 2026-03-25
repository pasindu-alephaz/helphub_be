package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lk.helphub.api.domain.enums.LanguageProficiency;

public class UserLanguageRequest {

    @Schema(description = "BCP-47 language code (e.g. 'si', 'ta', 'en') or empty for custom entries", example = "si")
    private String languageCode;

    @NotBlank(message = "Language name is required")
    @Schema(description = "Language name (use 'Other' for custom), supports Sinhala/Tamil/English Unicode",
            example = "සිංහල", requiredMode = Schema.RequiredMode.REQUIRED)
    private String languageName;

    @NotNull(message = "Proficiency level is required")
    @Schema(description = "Proficiency level: BASIC, CONVERSATIONAL, FLUENT, NATIVE",
            example = "NATIVE", requiredMode = Schema.RequiredMode.REQUIRED)
    private LanguageProficiency proficiency;

    public UserLanguageRequest() {}

    public UserLanguageRequest(String languageCode, String languageName, LanguageProficiency proficiency) {
        this.languageCode = languageCode;
        this.languageName = languageName;
        this.proficiency = proficiency;
    }

    public String getLanguageCode() { return languageCode; }
    public void setLanguageCode(String languageCode) { this.languageCode = languageCode; }

    public String getLanguageName() { return languageName; }
    public void setLanguageName(String languageName) { this.languageName = languageName; }

    public LanguageProficiency getProficiency() { return proficiency; }
    public void setProficiency(LanguageProficiency proficiency) { this.proficiency = proficiency; }

    public static UserLanguageRequestBuilder builder() {
        return new UserLanguageRequestBuilder();
    }

    public static class UserLanguageRequestBuilder {
        private String languageCode;
        private String languageName;
        private LanguageProficiency proficiency;

        public UserLanguageRequestBuilder languageCode(String languageCode) { this.languageCode = languageCode; return this; }
        public UserLanguageRequestBuilder languageName(String languageName) { this.languageName = languageName; return this; }
        public UserLanguageRequestBuilder proficiency(LanguageProficiency proficiency) { this.proficiency = proficiency; return this; }

        public UserLanguageRequest build() {
            return new UserLanguageRequest(languageCode, languageName, proficiency);
        }
    }
}
