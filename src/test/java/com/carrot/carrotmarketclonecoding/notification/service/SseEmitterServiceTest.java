package com.carrot.carrotmarketclonecoding.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.carrot.carrotmarketclonecoding.notification.domain.enums.NotificationType;
import com.carrot.carrotmarketclonecoding.notification.repository.SseEmitterRepository;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@ExtendWith(MockitoExtension.class)
class SseEmitterServiceTest {

    @Mock
    private SseEmitterRepository emitterRepository;

    @InjectMocks
    private SseEmitterService sseEmitterService;

    @Test
    @DisplayName("SSE 연결 테스트")
    void subscribeSuccess() {
        // given
        Long authId = 1111L;
        String lastEventId = "LastEventId";
        when(emitterRepository.save(any(), any())).thenReturn(null);

        // when
        SseEmitter resultEmitter = sseEmitterService.subscribe(1111L, lastEventId);

        // then
        verify(emitterRepository, times(1)).deleteAllEmitterStartWithId(String.valueOf(authId));
        verify(emitterRepository, times(1)).save(any(), any());
        verify(emitterRepository, times(1)).findAllEventCacheStartWithByMemberId(eq(String.valueOf(authId)));
        assertThat(resultEmitter).isNotNull();
    }

    @Test
    @DisplayName("SSE 알림 전송 테스트")
    public void sendSuccess() throws IOException {
        // given
        Long authId = 1111L;
        SseEmitter emitter = mock(SseEmitter.class);
        Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
        emitters.put("emitterId", emitter);
        when(emitterRepository.findAllEmitterStartWithByMemberId(eq(String.valueOf(authId)))).thenReturn(emitters);

        String content = "test message";

        // when
        sseEmitterService.send(authId, NotificationType.CONNECT, content);

        // then
        verify(emitter, times(1)).send(any(SseEmitter.SseEventBuilder.class));
        verify(emitterRepository, times(1)).saveEventCache(any(), eq(content));
    }
}