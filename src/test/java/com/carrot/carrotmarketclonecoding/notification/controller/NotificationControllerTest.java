package com.carrot.carrotmarketclonecoding.notification.controller;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.MEMBER_NOT_FOUND;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.NOTIFICATION_NOT_EXISTS;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.UNAUTHORIZED_ACCESS;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.GET_ALL_NOTIFICATIONS_SUCCESS;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.READ_NOTIFICATION_SUCCESS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrot.carrotmarketclonecoding.auth.config.WithCustomMockUser;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.NotificationNotExistsException;
import com.carrot.carrotmarketclonecoding.common.exception.UnauthorizedAccessException;
import com.carrot.carrotmarketclonecoding.notification.dto.NotificationResponseDto;
import com.carrot.carrotmarketclonecoding.notification.service.SseEmitterService;
import com.carrot.carrotmarketclonecoding.notification.service.impl.NotificationServiceImpl;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
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

    @Nested
    @DisplayName("SSE 연결 요청 컨트롤러 테스트")
    class ConnectSuccess {

        @Test
        @DisplayName("성공")
        void connectSuccess() throws Exception {
            // given
            // when
            when(sseEmitterService.subscribe(anyLong(), anyString())).thenReturn(mock(SseEmitter.class));

            // then
            mvc.perform(get("/connect"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("이전 알림 내용 전체 조회 컨트롤러 테스트")
    class GetAllNotifications {

        @Test
        @DisplayName("성공")
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

        @Test
        @DisplayName("실패 - 사용자가 존재하지 않음")
        void getAllNotificationsFailMemberNotFound() throws Exception {
            // given
            // when
            doThrow(MemberNotFoundException.class).when(notificationService).read(anyLong(), anyLong());

            // then
            mvc.perform(patch("/notification/{id}", 1L)
                            .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status", equalTo(401)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(MEMBER_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }
    }


    @Nested
    @DisplayName("알림 읽음 처리 컨트롤러 테스트")
    class Read {

        @Test
        @DisplayName("성공")
        void readSuccess() throws Exception {
            // given
            // when
            doNothing().when(notificationService).read(anyLong(), anyLong());

            // then
            mvc.perform(patch("/notification/{id}", 1L)
                            .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", equalTo(200)))
                    .andExpect(jsonPath("$.result", equalTo(true)))
                    .andExpect(jsonPath("$.message", equalTo(READ_NOTIFICATION_SUCCESS.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName("성공 - 사용자가 존재하지 않음")
        void readFailMemberNotFound() throws Exception {
            // given
            // when
            doThrow(MemberNotFoundException.class).when(notificationService).read(anyLong(), anyLong());

            // then
            mvc.perform(patch("/notification/{id}", 1L)
                            .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status", equalTo(401)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(MEMBER_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName("실패 - 알림이 존재하지 않음")
        void readFailNotificationNotFound() throws Exception {
            // given
            // when
            doThrow(NotificationNotExistsException.class).when(notificationService).read(anyLong(), anyLong());

            // then
            mvc.perform(patch("/notification/{id}", 1L)
                            .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(NOTIFICATION_NOT_EXISTS.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName("실패 - 사용자의 알림이 아님")
        void readFailNotMemberOfNotification() throws Exception {
            // given
            // when
            doThrow(UnauthorizedAccessException.class).when(notificationService).read(anyLong(), anyLong());

            // then
            mvc.perform(patch("/notification/{id}", 1L)
                            .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.status", equalTo(403)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(UNAUTHORIZED_ACCESS.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }
    }
}