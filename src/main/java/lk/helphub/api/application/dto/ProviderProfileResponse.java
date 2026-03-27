package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response containing provider profile information")
public class ProviderProfileResponse {

    @Schema(description = "ID of the user", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID userId;

    @Schema(description = "Business or display name", example = "John's Services")
    private String businessName;

    @Schema(description = "Professional bio", example = "Experienced plumber.")
    private String bio;

    @Schema(description = "Identity verification documents")
    private List<ProviderIdentityResponse> identityDocuments;

    @Schema(description = "Services offered by the provider")
    private List<ProviderServiceResponse> services;

    @Schema(description = "Portfolio items")
    private List<ProviderPortfolioResponse> portfolio;
}
