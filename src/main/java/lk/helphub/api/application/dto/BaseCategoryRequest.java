package lk.helphub.api.application.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseCategoryRequest {
    private Map<String, String> name;
    private Map<String, String> description;
    @Builder.Default
    private String status = "active";
    @Builder.Default
    private Integer displayOrder = 0;
    private UUID iconId;
}
