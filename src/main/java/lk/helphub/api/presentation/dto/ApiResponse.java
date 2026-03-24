package lk.helphub.api.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lk.helphub.api.domain.enums.ResponseStatusCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Generic API response wrapper")
public class ApiResponse<T> {

    @Schema(description = "Indicates whether the API request was successful or not", example = "true")
    private boolean status;

    @JsonProperty("status_code")
    @Schema(description = "Internal application-specific status code mapping to business logic outcomes", example = "SUCCESS")
    private ResponseStatusCode statusCode;

    @Schema(description = "Human-readable message providing more details about the outcome", example = "Operation completed successfully")
    private String message;

    @Schema(description = "Map of field/property names to arrays of error messages, populated during validation failures", hidden = true)
    private Map<String, List<String>> errors;

    @Schema(description = "The payload/data returned by the endpoint, shape depends on the exact endpoint")
    private T data;
}
