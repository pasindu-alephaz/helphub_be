package lk.helphub.api.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "User address response")
public class UserAddressResponse {

    @Schema(description = "Address ID")
    private UUID id;

    @Schema(description = "Label for this address, e.g. Home, Work", example = "Home")
    private String label;

    @Schema(description = "Province", example = "Western")
    private String province;

    @Schema(description = "District", example = "Colombo")
    private String district;

    @Schema(description = "City", example = "Colombo 03")
    private String city;

    @Schema(description = "Country", example = "Sri Lanka")
    private String country;

    @Schema(description = "Postal / zip code", example = "00300")
    private String postalCode;

    @Schema(description = "GPS latitude", example = "6.9271")
    private BigDecimal latitude;

    @Schema(description = "GPS longitude", example = "79.8612")
    private BigDecimal longitude;

    @Schema(description = "Formatted location string", example = "No 123, Galle Road, Colombo 03")
    private String location;

    @Schema(description = "Whether this is the default address for job requests", example = "true")
    private boolean isDefault;
}
