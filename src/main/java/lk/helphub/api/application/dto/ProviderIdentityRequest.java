package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request for identity document upload")
public class ProviderIdentityRequest {

    @Schema(description = "Type of document (NIC, PASSPORT, DRIVING_LICENSE)", example = "NIC")
    @NotBlank(message = "Document type is required")
    private String documentType;

    @Schema(description = "Issuing country or region", example = "LK")
    @NotBlank(message = "Issuing country is required")
    private String issuingCountry;

    @Schema(description = "Issuing country code", example = "+94")
    private String issuingCountryCode;

    @Schema(description = "List of image URLs or IDs for this document")
    private List<String> imageUrls;
}
