package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response containing identity document details")
public class ProviderIdentityResponse {

    @Schema(description = "ID of the document record", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Type of document", example = "NIC")
    private String documentType;

    @Schema(description = "Issuing country", example = "LK")
    private String issuingCountry;

    @Schema(description = "Issuing country code", example = "+94")
    private String issuingCountryCode;

    @Schema(description = "Status of verification", example = "PENDING")
    private String status;

    @Schema(description = "List of image URLs for this document")
    private java.util.List<String> imageUrls;
}
