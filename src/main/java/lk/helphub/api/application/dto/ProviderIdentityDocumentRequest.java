package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request payload for adding an identity document")
public class ProviderIdentityDocumentRequest {

    @NotBlank(message = "Document type is required")
    @Schema(description = "Type of the document (e.g., NIC, PASSPORT, DRIVING_LICENSE)")
    private String documentType;

    @NotBlank(message = "Issuing country is required")
    private String issuingCountry;

    @NotBlank(message = "Document code is required")
    private String documentCode;
}
