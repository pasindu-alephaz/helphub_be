package lk.helphub.api.admin.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobStatsResponse {
    private long totalJobs;
    private long openJobs;
    private long inProgressJobs;
    private long completedJobs;
    private long cancelledJobs;
    private long urgentJobs;
    private BigDecimal averagePrice;
}
