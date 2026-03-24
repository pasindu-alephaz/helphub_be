package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.NotificationResponse;
import lk.helphub.api.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationService {
    Page<NotificationResponse> getNotifications(User user, Pageable pageable);
    void markAsRead(UUID id, User user);
    void markAllAsRead(User user);
    void deleteNotification(UUID id, User user);
    void sendNotification(User user, String title, String message, String payload);
}
