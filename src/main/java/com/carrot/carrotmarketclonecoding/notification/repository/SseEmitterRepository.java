package com.carrot.carrotmarketclonecoding.notification.repository;

import java.util.Map;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseEmitterRepository {
    SseEmitter save(String emitterId, SseEmitter sseEmitter);

    void saveEventCache(String emitterId, Object event);

    Map<String, SseEmitter> findAllEmitterStartWithByMemberId(String memberId);

    Map<String,Object> findAllEventCacheStartWithByMemberId(String memberId);

    void deleteById(String emitterId);

    void deleteAllEmitterStartWithId(String memberId);

    void deleteAllEventCacheStartWithId(String memberId);
}
