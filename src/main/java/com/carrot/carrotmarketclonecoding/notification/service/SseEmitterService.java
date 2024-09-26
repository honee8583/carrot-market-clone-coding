package com.carrot.carrotmarketclonecoding.notification.service;

import com.carrot.carrotmarketclonecoding.notification.domain.enums.NotificationType;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
public class SseEmitterService {

    @Value("${sse.timeout}")
    private Long timeout;

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter add(Long authId) {
        SseEmitter emitter = new SseEmitter(timeout);
        this.emitters.put(authId, emitter);

        log.debug("Sending Dummy Data...");
        send(authId, NotificationType.NOTICE, "connected!");

        log.debug("SSE EMITTER -> authID: {}, emitter: {}, size: {}", authId, emitter, emitters.size());

        emitter.onCompletion(() -> {
            log.debug("SseEmitter onCompletion callback");
            this.emitters.remove(authId);
        });

        emitter.onTimeout(() -> {
            log.debug("SseEmitter onTimeout callback");
            emitter.complete();
        });

        emitter.onError(throwable -> {
            log.debug("SseEmitter Error occurred!");
            emitter.complete();
        });

        return emitter;
    }

    public void send(Long id, NotificationType type, String content) {
        log.debug("Map에 저장되어 있는가? {}", emitters.containsKey(id));
        SseEmitter emitter = emitters.get(id);
        log.debug("저장되어 있는 emitter 정보 : {}", emitter.toString());
        try {
            log.debug("알림을 전송합니다!");
            emitter.send(SseEmitter.event()
                    .name(type.name())
                    .data(content));
        } catch (IOException e) {
            log.error("알림 전송에 실패하였습니다!");
            throw new RuntimeException(e);
        }
    }
}