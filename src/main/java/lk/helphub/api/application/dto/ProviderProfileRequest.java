package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lk.helphub.api.domain.enums.Gender;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "Request payload for creating/updating a provider profile")
public class ProviderProfileRequest {

    @Valid
    @NotNull(message = "Personal details are required")
    private PersonalDetailsRequest personalDetails;

    @Valid
    @NotNull(message = "Address details are required")
    private AddressDetailsRequest addressDetails;

    @Schema(description = "Optional long-form professional bio")
    private String professionalBio;
}
