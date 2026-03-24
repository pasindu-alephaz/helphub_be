package lk.helphub.api.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lk.helphub.api.domain.enums.LanguageProficiency;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Communication language preference response")
public class UserLanguageResponse {

    @Schema(description = "Language entry ID")
    private UUID id;

    @Schema(description = "BCP-47 language code", example = "si")
    private String languageCode;

    @Schema(description = "Language name (may be Sinhala, Tamil, English, or custom)", example = "සිංහල")
    private String languageName;

    @Schema(description = "Proficiency level", example = "NATIVE")
    private LanguageProficiency proficiency;

    public UserLanguageResponse() {}

    public UserLanguageResponse(UUID id, String languageCode, String languageName, LanguageProficiency proficiency) {
        this.id = id;
        this.languageCode = languageCode;
        this.languageName = languageName;
        this.proficiency = proficiency;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getLanguageCode() { return languageCode; }
    public void setLanguageCode(String languageCode) { this.languageCode = languageCode; }

    public String getLanguageName() { return languageName; }
    public void setLanguageName(String languageName) { this.languageName = languageName; }

    public LanguageProficiency getProficiency() { return proficiency; }
    public void setProficiency(LanguageProficiency proficiency) { this.proficiency = proficiency; }

    public static UserLanguageResponseBuilder builder() {
        return new UserLanguageResponseBuilder();
    }

    public static class UserLanguageResponseBuilder {
        private UUID id;
        private String languageCode;
        private String languageName;
        private LanguageProficiency proficiency;

        public UserLanguageResponseBuilder id(UUID id) { this.id = id; return this; }
        public UserLanguageResponseBuilder languageCode(String languageCode) { this.languageCode = languageCode; return this; }
        public UserLanguageResponseBuilder languageName(String languageName) { this.languageName = languageName; return this; }
        public UserLanguageResponseBuilder proficiency(LanguageProficiency proficiency) { this.proficiency = proficiency; return this; }

        public UserLanguageResponse build() {
            return new UserLanguageResponse(id, languageCode, languageName, proficiency);
        }
    }
}
