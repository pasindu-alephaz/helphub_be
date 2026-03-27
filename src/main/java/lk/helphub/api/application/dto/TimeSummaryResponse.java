package lk.helphub.api.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSummaryResponse {
    private UUID jobId;
    private Integer totalWorkMinutes;
    private List<SessionSummary> sessions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SessionSummary {
        private UUID id;
        private Integer sessionNumber;
        private LocalDateTime startedAt;
        private LocalDateTime endedAt;
        private Integer durationMinutes;
        private String pauseReason;
    }
}
