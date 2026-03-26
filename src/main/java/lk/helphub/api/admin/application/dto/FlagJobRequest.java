package lk.helphub.api.admin.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlagJobRequest {
    @NotBlank(message = "Reason is required")
    private String reason;
}
