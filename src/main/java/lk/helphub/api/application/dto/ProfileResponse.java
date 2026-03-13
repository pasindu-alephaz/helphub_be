package lk.helphub.api.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "User profile response details")
public class ProfileResponse {
    @Schema(description = "Unique identifier of the user", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;
    
    @Schema(description = "User's first name", example = "John")
    private String firstName;
    
    @Schema(description = "User's last name", example = "Doe")
    private String lastName;
    
    @Schema(description = "User's email address", example = "john.doe@example.com")
    private String email;
    
    @Schema(description = "User's phone number", example = "+1234567890")
    private String phoneNumber;
    
    @Schema(description = "Detailed bio or about me section", example = "Software Engineer with 5 years of experience.")
    private String bio;
    
    @Schema(description = "Type of user account", example = "REGULAR")
    private String userType;
    
    @Schema(description = "Status of the user account", example = "ACTIVE")
    private String status;
    
    @Schema(description = "Timestamp when the profile was created", example = "2023-10-27T10:00:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Timestamp when the profile was last updated", example = "2023-10-27T10:00:00")
    private LocalDateTime updatedAt;
}
