package lk.helphub.api.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private UUID id;
    @Schema(description = "Name translations by language code (ISO 639-1: en=English, si=Sinhala, ta=Tamil)", example = "{\"en\": \"Category Name EN\", \"si\": \"Category Name SI\", \"ta\": \"Category Name TA\"}")
    private Map<String, String> name;
    @Schema(description = "Description translations by language code (ISO 639-1: en=English, si=Sinhala, ta=Tamil)", example = "{\"en\": \"Description EN\", \"si\": \"Description SI\", \"ta\": \"Description TA\"}")
    private Map<String, String> description;
    private String status;
    private Integer displayOrder;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID parentId;
    
    private UUID iconId;
    private String iconUrl;
    private List<CategoryResponse> subcategories;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
