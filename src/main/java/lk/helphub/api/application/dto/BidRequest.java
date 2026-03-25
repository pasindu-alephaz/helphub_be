package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BidRequest {

    @Schema(description = "Amount of the bid", example = "5000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Bid amount is required")
    @DecimalMin(value = "0.01", message = "Bid amount must be greater than zero")
    private BigDecimal amount;

    @Schema(description = "Proposal message from the provider", example = "I can do this job efficiently and have 5 years of experience.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Proposal is required")
    private String proposal;
}
