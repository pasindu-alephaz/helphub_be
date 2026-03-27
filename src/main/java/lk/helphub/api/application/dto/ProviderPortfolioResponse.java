package lk.helphub.api.application.dto;

import lombok.*;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderPortfolioResponse {
    private UUID id;
    private String title;
    private String description;
    private List<String> imageUrls;
}
