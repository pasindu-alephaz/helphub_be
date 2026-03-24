package lk.helphub.api.application.services;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.UUID;

public interface SseNotificationService {
    SseEmitter subscribe(UUID userId);
    void sendNotification(UUID userId, Object notification);
}
