package lk.helphub.api.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ProviderIdentityDocumentResponse {
    private UUID id;
    private String documentType;
    private String issuingCountry;
    private String documentCode;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
}
