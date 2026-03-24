package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseCategoryRequest {
    @Schema(
        description = "Name translations by language code (ISO 639-1: en=English, si=Sinhala, ta=Tamil)",
        example = "{\"en\": \"Category Name EN\", \"si\": \"Category Name SI\", \"ta\": \"Category Name TA\"}",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Map<String, String> name;
    
    @Schema(
        description = "Description translations by language code (ISO 639-1: en=English, si=Sinhala, ta=Tamil)",
        example = "{\"en\": \"Description EN\", \"si\": \"Description SI\", \"ta\": \"Description TA\"}"
    )
    private Map<String, String> description;
    
    private String status = "active";
    private Integer displayOrder = 0;
    
    @Schema(description = "Optional icon image ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID iconId;
}
