package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class BidResponse {

    private UUID id;
    private UUID jobId;
    private UUID providerId;
    private String providerName;
    private BigDecimal amount;
    private String proposal;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
