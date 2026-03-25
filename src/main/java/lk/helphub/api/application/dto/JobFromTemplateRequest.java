package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class JobFromTemplateRequest {

    @Schema(description = "Scheduled date and time for the job", example = "2024-12-31T10:00:00")
    private LocalDateTime scheduledAt;

    @Schema(description = "Override price for this specific job instance", example = "3500.00")
    private BigDecimal price;

    @Schema(description = "Override title for this specific job instance")
    private String title;

    @Schema(description = "Override description for this specific job instance")
    private String description;
}
