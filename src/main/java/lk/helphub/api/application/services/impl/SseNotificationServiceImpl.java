package lk.helphub.api.application.services.impl;

import lk.helphub.api.application.services.SseNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SseNotificationServiceImpl implements SseNotificationService {

    private final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();
    private static final Long TIMEOUT = 600000L; // 10 minutes

    @Override
    public SseEmitter subscribe(UUID userId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError((e) -> emitters.remove(userId));

        emitters.put(userId, emitter);
        
        // Send initial keep-alive or connection message
        try {
            emitter.send(SseEmitter.event().name("INIT").data("Connected"));
        } catch (IOException e) {
            log.warn("Error sending INIT event to user {}: {}", userId, e.getMessage());
            emitters.remove(userId);
        }

        return emitter;
    }

    @Override
    public void sendNotification(UUID userId, Object notification) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(notification));
            } catch (IOException e) {
                log.warn("Error sending notification to user {}: {}", userId, e.getMessage());
                emitters.remove(userId);
            }
        }
    }
}
