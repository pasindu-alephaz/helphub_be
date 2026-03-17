package lk.helphub.api.application.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseCategoryRequest {
    @Schema(description = "Multilingual names for the category", example = "{\"en\": \"Home Repairs\", \"si\": \"නිවාස අලුත්වැඩියාව\", \"ta\": \"வீட்டு பழுதுபார்த்தல்\"}")
    private Map<String, String> name;
    
    @Schema(description = "Multilingual descriptions for the category", example = "{\"en\": \"All types of home repair services\", \"si\": \"සියලුම වර්ගයේ නිවාස අලුත්වැඩියා සේවා\", \"ta\": \"அனைத்து வகை வீட்டு பழுதுபார்க்கும் சேவைகள்\"}")
    private Map<String, String> description;
    @Builder.Default
    private String status = "active";
    @Builder.Default
    private Integer displayOrder = 0;
    @Schema(description = "Optional icon image ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID iconId;

    public void setIconId(Object iconId) {
        if (iconId == null || (iconId instanceof String && ((String) iconId).trim().isEmpty())) {
            this.iconId = null;
        } else if (iconId instanceof String) {
            this.iconId = UUID.fromString((String) iconId);
        } else if (iconId instanceof UUID) {
            this.iconId = (UUID) iconId;
        }
    }
}
