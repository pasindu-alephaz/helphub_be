package lk.helphub.api.application.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SubcategoryCreateRequest extends BaseCategoryRequest {
    private UUID parentId;
}
