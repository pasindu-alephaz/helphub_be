package lk.helphub.api.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.helphub.api.application.dto.MessageRequest;
import lk.helphub.api.application.dto.MessageResponse;
import lk.helphub.api.application.dto.JobResponse;
import lk.helphub.api.application.services.MessageService;
import lk.helphub.api.domain.enums.ResponseStatusCode;
import lk.helphub.api.presentation.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs/{id}/messages")
@RequiredArgsConstructor
@Tag(name = "Job Negotiation", description = "APIs for job negotiation and chat")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    @Operation(summary = "Send a message", description = "Send a negotiation/chat message for a job")
    @PreAuthorize("hasAuthority('job_message')")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            Principal principal,
            @PathVariable UUID id,
            @Valid @RequestBody MessageRequest request
    ) {
        MessageResponse response = messageService.sendMessage(id, principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<MessageResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Message sent successfully")
                .data(response)
                .build());
    }

    @GetMapping
    @Operation(summary = "Get messages", description = "Get negotiation/chat history for a job")
    @PreAuthorize("hasAuthority('job_read')")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getMessages(
            Principal principal,
            @PathVariable UUID id
    ) {
        List<MessageResponse> response = messageService.getMessages(id, principal.getName());
        return ResponseEntity.ok(ApiResponse.<List<MessageResponse>>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Messages retrieved successfully")
                .data(response)
                .build());
    }

    @PostMapping("/{messageId}/accept")
    @Operation(summary = "Accept suggestion", description = "Accept a price or schedule suggestion from a message")
    @PreAuthorize("hasAuthority('job_update')")
    public ResponseEntity<ApiResponse<JobResponse>> acceptSuggestion(
            Principal principal,
            @PathVariable UUID id,
            @PathVariable UUID messageId
    ) {
        JobResponse response = messageService.acceptSuggestion(id, messageId, principal.getName());
        return ResponseEntity.ok(ApiResponse.<JobResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Suggestion accepted successfully")
                .data(response)
                .build());
    }
}
