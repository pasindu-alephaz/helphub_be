package lk.helphub.api.application.dto;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderCertificateRequest {
    @NotBlank(message = "Certificate name is required")
    private String name;

    @NotBlank(message = "File URL is required")
    private String fileUrl;

    private LocalDate issuedDate;
}
