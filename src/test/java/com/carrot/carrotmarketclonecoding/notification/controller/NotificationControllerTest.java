package com.carrot.carrotmarketclonecoding.notification.controller;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.MEMBER_NOT_FOUND;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.NOTIFICATION_NOT_EXISTS;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.UNAUTHORIZED_ACCESS;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.GET_ALL_NOTIFICATIONS_SUCCESS;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.READ_NOTIFICATION_SUCCESS;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrot.carrotmarketclonecoding.ControllerTest;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.NotificationNotExistsException;
import com.carrot.carrotmarketclonecoding.common.exception.UnauthorizedAccessException;
import com.carrot.carrotmarketclonecoding.notification.dto.NotificationResponseDto;
import com.carrot.carrotmarketclonecoding.notification.helper.NotificationDtoFactory;
import com.carrot.carrotmarketclonecoding.notification.helper.NotificationTestHelper;
import com.carrot.carrotmarketclonecoding.util.ResultFields;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

class NotificationControllerTest extends ControllerTest {

    private NotificationTestHelper testHelper;

    @Autowired
    private NotificationDtoFactory dtoFactory;

    @BeforeEach
    void setUp() {
        this.testHelper = new NotificationTestHelper(mvc, restDocs);
    }

    @Nested
    @DisplayName("SSE 연결 요청 컨트롤러 테스트")
    class ConnectSuccess {

        @Test
        @DisplayName("성공")
        void connectSuccessTest() throws Exception {
            // given
            // when
            when(sseEmitterService.subscribe(anyLong(), anyString())).thenReturn(mock(SseEmitter.class));

            // then
            testHelper.assertConnectSuccess();
        }
    }

    @Nested
    @DisplayName("이전 알림 내용 전체 조회 컨트롤러 테스트")
    class GetAllNotifications {

        @Test
        @DisplayName("성공")
        void getAllNotificationsSuccessTest() throws Exception {
            // given
            List<NotificationResponseDto> response = dtoFactory.createNotifications();

            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(GET_ALL_NOTIFICATIONS_SUCCESS.getMessage())
                    .build();

            // when
            when(notificationService.getAllNotifications(anyLong())).thenReturn(response);

            // then
            testHelper.assertGetAllNotificationsSuccess(resultFields);
        }

        @Test
        @DisplayName("실패 - 사용자가 존재하지 않음")
        void getAllNotificationsFailMemberNotFoundTest() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(MemberNotFoundException.class).when(notificationService).getAllNotifications(anyLong());

            // then
            testHelper.assertGetAllNotificationsFail(resultFields);
        }
    }

    @Nested
    @DisplayName("알림 읽음 처리 컨트롤러 테스트")
    class Read {

        @Test
        @DisplayName("성공")
        void readSuccessTest() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(READ_NOTIFICATION_SUCCESS.getMessage())
                    .build();

            // when
            doNothing().when(notificationService).read(anyLong(), anyLong());

            // then
            testHelper.assertReadNotification(resultFields);
        }

        @Test
        @DisplayName("실패 - 사용자가 존재하지 않음")
        void readFailMemberNotFoundTest() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(MemberNotFoundException.class).when(notificationService).read(anyLong(), anyLong());

            // then
            testHelper.assertReadNotification(resultFields);
        }

        @Test
        @DisplayName("실패 - 알림이 존재하지 않음")
        void readFailNotificationNotFoundTest() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isBadRequest())
                    .status(400)
                    .result(false)
                    .message(NOTIFICATION_NOT_EXISTS.getMessage())
                    .build();

            // when
            doThrow(NotificationNotExistsException.class).when(notificationService).read(anyLong(), anyLong());

            // then
            testHelper.assertReadNotification(resultFields);
        }

        @Test
        @DisplayName("실패 - 사용자의 알림이 아님")
        void readFailNotMemberOfNotificationTest() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isForbidden())
                    .status(403)
                    .result(false)
                    .message(UNAUTHORIZED_ACCESS.getMessage())
                    .build();

            // when
            doThrow(UnauthorizedAccessException.class).when(notificationService).read(anyLong(), anyLong());

            // then
            testHelper.assertReadNotification(resultFields);
        }
    }
}