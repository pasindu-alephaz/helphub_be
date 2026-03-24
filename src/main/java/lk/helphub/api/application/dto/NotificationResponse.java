package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    @Schema(description = "Unique identifier of the notification", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID id;

    @Schema(description = "Title of the notification", example = "Job Accepted")
    private String title;

    @Schema(description = "Message content of the notification", example = "Your job request has been accepted by John Doe.")
    private String message;

    @Schema(description = "Additional custom data in JSON format", example = "{\"jobId\": \"...\"}")
    private String payload;

    @Schema(description = "Whether the notification has been read", example = "false")
    private boolean isRead;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;
}
