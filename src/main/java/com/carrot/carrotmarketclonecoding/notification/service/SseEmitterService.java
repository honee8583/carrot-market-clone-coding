package com.carrot.carrotmarketclonecoding.notification.service;

import com.carrot.carrotmarketclonecoding.notification.domain.enums.NotificationType;
import com.carrot.carrotmarketclonecoding.notification.repository.SseEmitterRepository;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseEmitterService {

    private final SseEmitterRepository emitterRepository;

    @Value("${sse.timeout}")
    private Long timeout;

    private static final String CONNECT_MESSAGE = "connected!";

    public SseEmitter subscribe(Long authId, String lastEventId) {
        emitterRepository.deleteAllEmitterStartWithId(String.valueOf(authId));

        String emitterId = createEmitterId(authId);
        SseEmitter emitter = new SseEmitter(timeout);
        emitterRepository.save(emitterId, emitter);
        sendToClient(emitter, emitterId, NotificationType.CONNECT, CONNECT_MESSAGE);

        log.debug("emitterId: {}", emitterId);

        emitter.onCompletion(() -> {
            log.debug(">> SseEmitter onCompletion callback");
            emitterRepository.deleteById(emitterId);
        });

        emitter.onTimeout(() -> {
            log.debug(">> SseEmitter onTimeout callback");
            emitterRepository.deleteById(emitterId);
            emitter.complete();
        });

        emitter.onError(throwable -> {
            log.debug(">> SseEmitter Error occurred!");
            emitterRepository.deleteById(emitterId);
            emitter.complete();
        });

        sendAfterLastEventId(authId, lastEventId, emitter);

        return emitter;
    }

    public void send(Long authId, NotificationType type, Object content) {
        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithByMemberId(String.valueOf(authId));
        sseEmitters.forEach(
                (key, emitter) -> {
                    String emitterId = createEmitterId(authId);
                    emitterRepository.saveEventCache(emitterId, content);
                    sendToClient(emitter, emitterId, type, content);
                }
        );
    }

    private void sendToClient(SseEmitter emitter, String emitterId, NotificationType type, Object data) {
        try {
            log.debug("알림을 전송합니다! -> {}", emitterId);
            emitter.send(SseEmitter.event()
                    .id(emitterId)
                    .name(type.name())
                    .data(data));
        } catch (IOException exception) {
            log.debug("알림 전송중 에러 발생...");
            emitterRepository.deleteById(emitterId);
        }
    }

    private String createEmitterId(Long authId) {
        return authId + "_" + System.currentTimeMillis();
    }

    private void sendAfterLastEventId(Long authId, String lastEventId, SseEmitter emitter) {
        if (!lastEventId.isEmpty()) {
            log.debug("LastEventId: {}", lastEventId);
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(authId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), NotificationType.LAST_EVENT, entry.getValue()));
        }
    }
}