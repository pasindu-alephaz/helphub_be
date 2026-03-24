package lk.helphub.api.application.dto;

import lk.helphub.api.domain.enums.SkillLevel;
import lombok.*;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderServiceRequest {
    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    private UUID subCategoryId;

    @NotNull(message = "Skill level is required")
    @Builder.Default
    private SkillLevel skillLevel = SkillLevel.BEGINNER;

    @Builder.Default
    private boolean isAvailable = true;

    private java.time.LocalDateTime startDateTime;
    private java.time.LocalDateTime endDateTime;
}
