package lk.helphub.api.application.dto;

import lk.helphub.api.domain.enums.VerificationStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderProfileResponse {
    private UUID id;
    private String name;
    private String businessName;
    private String profileImageUrl;
    private VerificationStatus verificationStatus;
    private boolean isVerifiedBadge;
    private boolean isAvailable;
    private BigDecimal averageRating;
    private Integer reviewCount;
    private List<String> serviceCategories;
    private List<String> identityDocumentImageUrls;
    private List<ProviderPortfolioRequest> portfolio;
}
