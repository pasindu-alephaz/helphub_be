package lk.helphub.api.application.services.impl;

import lk.helphub.api.application.dto.NotificationResponse;
import lk.helphub.api.application.services.NotificationService;
import lk.helphub.api.application.services.SseNotificationService;
import lk.helphub.api.domain.entity.Notification;
import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseNotificationService sseNotificationService;

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotifications(User user, Pageable pageable) {
        return notificationRepository.findByUserAndDeletedAtIsNullOrderByCreatedAtDesc(user, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public void markAsRead(UUID id, User user) {
        notificationRepository.findByIdAndUserAndDeletedAtIsNull(id, user).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    @Override
    @Transactional
    public void markAllAsRead(User user) {
        notificationRepository.markAllAsReadForUser(user);
    }

    @Override
    @Transactional
    public void deleteNotification(UUID id, User user) {
        notificationRepository.softDeleteByIdAndUser(id, user);
    }

    @Override
    @Transactional
    public void sendNotification(User user, String title, String message, String payload) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .payload(payload)
                .isRead(false)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        
        // Push to SSE emitter if user is connected
        sseNotificationService.sendNotification(user.getId(), mapToResponse(savedNotification));
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .payload(notification.getPayload())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
