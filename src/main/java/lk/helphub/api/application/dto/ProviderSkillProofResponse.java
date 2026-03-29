package lk.helphub.api.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ProviderSkillProofResponse {
    private UUID id;
    private UUID subcategoryId;
    private String title;
    private String description;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
}
