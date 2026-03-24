package lk.helphub.api.application.dto;

import lk.helphub.api.domain.enums.IdentityType;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderIdentityRequest {
    @NotNull(message = "Identity type is required")
    private IdentityType idType;

    @NotBlank(message = "Identity number is required")
    private String idNumber;

    private String frontImageUrl;
    private String backImageUrl;
    private String selfieImageUrl;
}
