package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request for adding or updating provider addresses")
public class ProviderAddressRequest {

    @Schema(description = "ID of the address (for edit/delete)", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Label for the address", example = "Home")
    private String label;

    @Schema(description = "Street address", example = "No 123, Main Street")
    @NotBlank(message = "Street address is required")
    private String streetAddress;

    @Schema(description = "City", example = "Colombo")
    @NotBlank(message = "City is required")
    private String city;

    @Schema(description = "Province", example = "Western")
    private String province;

    @Schema(description = "District", example = "Colombo")
    private String district;

    @Schema(description = "Zip code", example = "00100")
    private String zipCode;

    @Schema(description = "Country", example = "Sri Lanka")
    private String country;

    private double latitude;
    private double longitude;
}
