package lk.helphub.api.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lk.helphub.api.domain.enums.LanguageProficiency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
}
