package com.carrot.carrotmarketclonecoding.notification.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.carrot.carrotmarketclonecoding.notification.domain.enums.NotificationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@ExtendWith(MockitoExtension.class)
class SseEmitterServiceTest {

    @InjectMocks
    private SseEmitterService sseEmitterService;

    @Test
    @DisplayName("SSE 연결 테스트")
    void addSuccess() {
        // given
        try (MockedStatic<SseEmitter> mockedSseEmitter = mockStatic(SseEmitter.class)) {
            // given
            Long authId = 1111L;
            SseEmitter.SseEventBuilder eventBuilder = mock(SseEmitter.SseEventBuilder.class);
            when(SseEmitter.event()).thenReturn(eventBuilder);
            when(eventBuilder.name(anyString())).thenReturn(eventBuilder);
            when(eventBuilder.data(anyString())).thenReturn(eventBuilder);

            // when
            SseEmitter result = sseEmitterService.add(authId);

            // then
            mockedSseEmitter.verify(SseEmitter::event, times(1));
            verify(eventBuilder).name(NotificationType.NOTICE.name());
            verify(eventBuilder).data("connected!");
        }
    }
}