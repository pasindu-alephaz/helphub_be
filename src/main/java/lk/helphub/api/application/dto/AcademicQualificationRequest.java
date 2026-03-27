package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lk.helphub.api.domain.enums.EducationalLevel;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request for adding or updating academic qualifications")
public class AcademicQualificationRequest {

    @Schema(description = "ID of the qualification (for edit/delete)", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Educational level", example = "UNDERGRADUATE")
    @NotNull(message = "Educational level is required")
    private EducationalLevel educationalLevel;

    @Schema(description = "Name of the certificate", example = "BSc in Engineering")
    @NotBlank(message = "Certificate name is required")
    private String certificateName;

    @Schema(description = "University or Institution", example = "University of Moratuwa")
    @NotBlank(message = "University/Institution is required")
    private String university;
}
