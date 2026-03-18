package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request to add or update a user address")
public class UserAddressRequest {

    @NotBlank(message = "Label is required (e.g. Home, Work)")
    @Size(max = 50)
    @Schema(description = "Label for this address", example = "Home", requiredMode = Schema.RequiredMode.REQUIRED)
    private String label;

    @Schema(description = "Province", example = "Western")
    private String province;

    @Schema(description = "District", example = "Colombo")
    private String district;

    @Schema(description = "City", example = "Colombo 03")
    private String city;

    @Schema(description = "Postal / zip code", example = "00300")
    private String postalCode;

    @Schema(description = "GPS latitude", example = "6.9271")
    private BigDecimal latitude;

    @Schema(description = "GPS longitude", example = "79.8612")
    private BigDecimal longitude;

    @Schema(description = "Set as default address for job requests", example = "true")
    private boolean isDefault;
}
