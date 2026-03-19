package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body for updating a job")
public class JobUpdateRequest {

    @Schema(description = "Type of job: FIXED or BIDDING", example = "FIXED", allowableValues = {"FIXED", "BIDDING"})
    @Size(max = 20, message = "Job type must not exceed 20 characters")
    private String jobType;

    @Schema(description = "Date for the job", example = "2024-12-01")
    private LocalDate jobDate;

    @Schema(description = "Time for the job", example = "14:30:00")
    private LocalTime jobTime;

    @Schema(description = "Preferred price for the job", example = "1500.00")
    private BigDecimal preferredPrice;
}
