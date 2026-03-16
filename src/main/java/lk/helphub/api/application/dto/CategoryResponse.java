package lk.helphub.api.application.dto;

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
    private Map<String, String> name;
    private Map<String, String> description;
    private String status;
    private Integer displayOrder;
    private UUID parentId;
    private UUID iconId;
    private String iconUrl;
    private List<CategoryResponse> subcategories;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
