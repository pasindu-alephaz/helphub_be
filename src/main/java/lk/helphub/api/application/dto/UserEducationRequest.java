package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request payload for managing academic qualifications")
public class UserEducationRequest {

    @NotBlank(message = "Certificate name is required")
    private String certificateName;

    @NotBlank(message = "University or Institution is required")
    private String university;
}
