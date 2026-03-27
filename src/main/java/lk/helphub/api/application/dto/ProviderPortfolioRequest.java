package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request for adding provider portfolio items")
public class ProviderPortfolioRequest {

    @Schema(description = "Title of the portfolio item", example = "Bathroom Renovation")
    private String title;

    @Schema(description = "Description of the work", example = "Complete renovation of a luxury bathroom.")
    private String description;

    @Schema(description = "List of image URLs for the portfolio item")
    private List<String> imageUrls;
}
