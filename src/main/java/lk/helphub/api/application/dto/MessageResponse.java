package lk.helphub.api.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {

    private UUID id;
    private UUID jobId;
    private UUID senderId;
    private String senderName;
    private String content;
    private BigDecimal suggestedPrice;
    private LocalDateTime suggestedScheduledAt;
    private String suggestedAvailabilityDuration;
    private String suggestionStatus;
    private LocalDateTime createdAt;
}
