package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for address details")
public class AddressDetailsRequest {
    @NotBlank(message = "Street address is required")
    private String streetAddress;
    
    @NotBlank(message = "City is required")
    private String city;
    
    @NotBlank(message = "Province is required")
    private String province;
    
    @NotBlank(message = "Zip code is required")
    private String zipCode;
    
    @NotBlank(message = "Country is required")
    private String country;
    
    @NotNull(message = "Latitude is required")
    private BigDecimal latitude;
    
    @NotNull(message = "Longitude is required")
    private BigDecimal longitude;
}
