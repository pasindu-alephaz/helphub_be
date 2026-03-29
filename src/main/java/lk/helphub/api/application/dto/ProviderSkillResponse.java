package lk.helphub.api.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ProviderSkillResponse {
    private UUID id;
    private UUID subcategoryId;
    private java.util.Map<String, String> subcategoryName;
    private String skillLevel;
    private LocalDateTime createdAt;
}
