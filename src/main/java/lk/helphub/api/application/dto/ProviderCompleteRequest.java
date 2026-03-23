package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body for provider marking a job as complete")
public class ProviderCompleteRequest {

    @Schema(description = "Optional remarks or notes about the completed work", example = "Finished the cleaning, all rooms are done.")
    private String remarks;

    @Schema(description = "Optional list of image IDs showing the completed work")
    private List<String> completionImages;
}
