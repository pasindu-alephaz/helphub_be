package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response containing provider service details")
public class ProviderServiceResponse {

    @Schema(description = "ID of the provider service record", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Category ID", example = "550e8400-e29b-41d4-a716-446655440001")
    private UUID categoryId;

    @Schema(description = "Subcategory ID", example = "550e8400-e29b-41d4-a716-446655440002")
    private UUID subcategoryId;

    @Schema(description = "Relationship/Specialization", example = "Residential Plumbing")
    private String relationship;
}
