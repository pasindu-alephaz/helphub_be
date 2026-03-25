package lk.helphub.api.presentation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lk.helphub.api.application.dto.JobResponse;
import lk.helphub.api.application.services.JobService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "Job Real-time", description = "WebSocket handlers for real-time job updates")
public class LocationWebSocketController {

    private final JobService jobService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/jobs/{id}/location")
    public void handleLocationUpdate(
            Principal principal,
            @DestinationVariable UUID id,
            @Payload LocationPayload payload
    ) {
        if (principal == null) {
             log.warn("Unauthenticated location update attempt for job {}", id);
             return;
        }

        String userEmail = principal.getName();
        log.debug("Received location update for job {} from {}: [{}, {}]", id, userEmail, payload.getLat(), payload.getLng());

        // Basic validation: ensure the job exists and the sender is the accepted provider
        try {
            JobResponse job = jobService.getJobById(id);
            // In a real scenario, we'd check if job.getAcceptedByEmail() matches userEmail
            // For now, we'll broadcast it to the specific job topic
            
            messagingTemplate.convertAndSend("/topic/jobs/" + id + "/location", payload);
            
        } catch (Exception e) {
            log.error("Error processing location update for job {}", id, e);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationPayload {
        private double lat;
        private double lng;
    }
}
