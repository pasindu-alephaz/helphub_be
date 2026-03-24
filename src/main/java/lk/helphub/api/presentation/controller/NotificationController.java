package lk.helphub.api.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lk.helphub.api.application.dto.NotificationResponse;
import lk.helphub.api.application.services.NotificationService;
import lk.helphub.api.application.services.SseNotificationService;
import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.enums.ResponseStatusCode;
import lk.helphub.api.domain.exception.ResourceNotFoundException;
import lk.helphub.api.domain.repository.UserRepository;
import lk.helphub.api.presentation.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification management and real-time streaming APIs")
public class NotificationController {

    private final NotificationService notificationService;
    private final SseNotificationService sseNotificationService;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "List notifications", description = "Retrieve a paginated list of notifications for the current user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotifications(
            Principal principal,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        User user = resolveUser(principal);
        Page<NotificationResponse> notifications = notificationService.getNotifications(user, pageable);
        return ResponseEntity.ok(ApiResponse.<Page<NotificationResponse>>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Notifications retrieved successfully")
                .data(notifications)
                .build());
    }

    @PutMapping("/mark-as-read/{id}")
    @Operation(summary = "Mark as read", description = "Mark a specific notification as read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            Principal principal,
            @PathVariable UUID id
    ) {
        User user = resolveUser(principal);
        notificationService.markAsRead(id, user);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Notification marked as read")
                .build());
    }

    @PutMapping("/mark-as-read")
    @Operation(summary = "Mark all as read", description = "Mark all unread notifications for the current user as read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(Principal principal) {
        User user = resolveUser(principal);
        notificationService.markAllAsRead(user);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("All notifications marked as read")
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete notification", description = "Soft delete a specific notification")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            Principal principal,
            @PathVariable UUID id
    ) {
        User user = resolveUser(principal);
        notificationService.deleteNotification(id, user);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Notification deleted successfully")
                .build());
    }

    @GetMapping(value = "/stream", produces = "text/event-stream")
    @Operation(summary = "Notification stream", description = "Subscribe to real-time notification updates using SSE")
    @PreAuthorize("isAuthenticated()")
    public SseEmitter streamNotifications(Principal principal) {
        User user = resolveUser(principal);
        return sseNotificationService.subscribe(user.getId());
    }

    private User resolveUser(Principal principal) {
        String identifier = principal.getName();
        return userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByPhoneNumber(identifier))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + identifier));
    }
}
