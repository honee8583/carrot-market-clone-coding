package com.carrot.carrotmarketclonecoding.notification.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.carrot.carrotmarketclonecoding.notification.repository.SseEmitterRepository;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

class SseEmitterRepositoryTest {

    private SseEmitterRepository sseEmitterRepository = new SseEmitterRepositoryImpl();

    private static final String EMITTER_ID = "test-emitter";

    @Test
    @DisplayName("SseEmitter 객체 저장 테스트")
    public void saveEmitter() {
        // given
        SseEmitter sseEmitter = new SseEmitter();

        // when
        SseEmitter result = sseEmitterRepository.save(EMITTER_ID, sseEmitter);

        // then
        assertThat(sseEmitter).isEqualTo(result);
        assertThat(sseEmitterRepository.findAllEmitterStartWithByMemberId("test")).containsKey(EMITTER_ID);
    }

    @Test
    @DisplayName("사용자의 알림 캐시 저장 테스트")
    public void saveEventCache() {
        // given
        Object event = "test";

        // when
        sseEmitterRepository.saveEventCache(EMITTER_ID, event);

        // then
        assertThat(sseEmitterRepository.findAllEventCacheStartWithByMemberId("test")).containsKey(EMITTER_ID);
    }

    @Test
    @DisplayName("사용자 아이디로 시작하는 모든 SseEmitter 조회 테스트")
    public void findAllEmitterStartWithByMemberId() {
        // given
        sseEmitterRepository.save(EMITTER_ID, new SseEmitter());

        // when
        Map<String, SseEmitter> emitters = sseEmitterRepository.findAllEmitterStartWithByMemberId("test");

        // then
        assertThat(emitters.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("사용자 아이디로 시작하는 모든 알림 캐시 조회 테스트")
    void findAllEventCacheStartWithByMemberId() {
        // given
        sseEmitterRepository.saveEventCache(EMITTER_ID, "test");

        // when
        Map<String, Object> cache = sseEmitterRepository.findAllEventCacheStartWithByMemberId("test");

        // then
        assertThat(cache.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("emitterId에 해당하는 SseEmitter 삭제")
    void deleteById() {
        // given
        sseEmitterRepository.save(EMITTER_ID, new SseEmitter());

        // when
        sseEmitterRepository.deleteById(EMITTER_ID);

        // then
        Map<String, SseEmitter> emitters = sseEmitterRepository.findAllEmitterStartWithByMemberId("test");
        assertThat(emitters.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("사용자 아이디로 시작하는 모든 SseEmitter 삭제")
    void deleteAllEmitterStartWithId() {
        // given
        sseEmitterRepository.save(EMITTER_ID, new SseEmitter());
        sseEmitterRepository.save(EMITTER_ID, new SseEmitter());

        // when
        sseEmitterRepository.deleteAllEmitterStartWithId(EMITTER_ID);

        // then
        Map<String, SseEmitter> emitters = sseEmitterRepository.findAllEmitterStartWithByMemberId("test");
        assertThat(emitters.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("사용자 아이디로 시작하는 모든 알림 캐시 삭제")
    void deleteAllEventCacheStartWithId() {
        // given
        sseEmitterRepository.saveEventCache(EMITTER_ID, "event1");
        sseEmitterRepository.saveEventCache(EMITTER_ID, "event2");
        sseEmitterRepository.saveEventCache(EMITTER_ID, "event3");

        // when
        sseEmitterRepository.deleteAllEventCacheStartWithId(EMITTER_ID);

        // then
        Map<String, Object> cache = sseEmitterRepository.findAllEventCacheStartWithByMemberId("test");
        assertThat(cache.size()).isEqualTo(0);
    }
}