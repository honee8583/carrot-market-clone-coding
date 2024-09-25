package com.carrot.carrotmarketclonecoding.notification.controller;

import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.GET_ALL_NOTIFICATIONS_SUCCESS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrot.carrotmarketclonecoding.auth.config.WithCustomMockUser;
import com.carrot.carrotmarketclonecoding.notification.dto.NotificationResponseDto;
import com.carrot.carrotmarketclonecoding.notification.service.SseEmitterService;
import com.carrot.carrotmarketclonecoding.notification.service.impl.NotificationServiceImpl;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@WithCustomMockUser
@WebMvcTest(controllers = NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SseEmitterService sseEmitterService;

    @MockBean
    private NotificationServiceImpl notificationService;

    @Test
    @DisplayName("SSE 연결 요청 테스트")
    void connectSuccess() throws Exception {
        // given
        // when
        when(sseEmitterService.add(anyLong())).thenReturn(mock(SseEmitter.class));

        // then
        mvc.perform(get("/connect"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("이전 알림 내용 전체 조회 테스트")
    void getAllNotificationsSuccess() throws Exception {
        // given
        List<NotificationResponseDto> response = Arrays.asList(
            NotificationResponseDto.builder().id(1L).content("notification1").build(),
            NotificationResponseDto.builder().id(1L).content("notification1").build(),
            NotificationResponseDto.builder().id(1L).content("notification1").build()
        );

        // when
        when(notificationService.getAllNotifications(anyLong())).thenReturn(response);

        // then
        mvc.perform(get("/notification"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", equalTo(200)))
                .andExpect(jsonPath("$.result", equalTo(true)))
                .andExpect(jsonPath("$.message", equalTo(GET_ALL_NOTIFICATIONS_SUCCESS.getMessage())))
                .andExpect(jsonPath("$.data.size()", equalTo(3)));
    }
}