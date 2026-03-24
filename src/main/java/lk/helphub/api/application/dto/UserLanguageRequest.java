package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lk.helphub.api.domain.enums.LanguageProficiency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request to add or update a communication language preference")
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
}
