package lk.helphub.api.application.dto;

import lombok.*;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderPortfolioRequest {
    private java.util.List<String> imageUrls;

    private String title;
    private String description;
}
