package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageRequest {

    @Schema(description = "Content of the message", example = "Hi, can we schedule this for tomorrow morning?", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Message content is required")
    private String content;

    @Schema(description = "Suggested price update (optional)", example = "4500.00")
    private BigDecimal suggestedPrice;

    @Schema(description = "Suggested schedule update (optional)", example = "2024-12-01T10:00:00")
    private LocalDateTime suggestedScheduledAt;

    @Schema(description = "Suggested availability duration update (optional)", example = "3 hours")
    private String suggestedAvailabilityDuration;
}
