package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request to update the app language preference")
public class LanguagePreferenceRequest {

    @NotBlank(message = "Language preference is required")
    @Pattern(regexp = "^(SINHALA|ENGLISH|TAMIL)$", message = "Language must be one of: SINHALA, ENGLISH, TAMIL")
    @Schema(description = "Language preference for localized app content",
            example = "SINHALA",
            allowableValues = {"SINHALA", "ENGLISH", "TAMIL"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String languagePreference;
}
