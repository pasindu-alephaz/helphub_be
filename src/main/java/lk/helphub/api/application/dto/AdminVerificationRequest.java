package lk.helphub.api.application.dto;

import lk.helphub.api.domain.enums.VerificationStatus;
import lombok.*;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminVerificationRequest {
    @NotNull(message = "Status is required")
    private VerificationStatus status;

    private String notes;
}
