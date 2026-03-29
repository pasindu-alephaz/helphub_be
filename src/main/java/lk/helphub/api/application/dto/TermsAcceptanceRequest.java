package lk.helphub.api.application.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TermsAcceptanceRequest {
    @AssertTrue(message = "You must accept the terms and conditions")
    private boolean accepted;

    @NotBlank(message = "Terms version is required")
    private String version;
}
