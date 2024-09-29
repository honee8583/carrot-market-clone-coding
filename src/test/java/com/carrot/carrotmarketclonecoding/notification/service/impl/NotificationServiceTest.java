package com.carrot.carrotmarketclonecoding.notification.service.impl;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.MEMBER_NOT_FOUND;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.NOTIFICATION_NOT_EXISTS;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.UNAUTHORIZED_ACCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.NotificationNotExistsException;
import com.carrot.carrotmarketclonecoding.common.exception.UnauthorizedAccessException;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import com.carrot.carrotmarketclonecoding.notification.domain.Notification;
import com.carrot.carrotmarketclonecoding.notification.domain.enums.NotificationType;
import com.carrot.carrotmarketclonecoding.notification.dto.NotificationResponseDto;
import com.carrot.carrotmarketclonecoding.notification.repository.NotificationRepository;
import com.carrot.carrotmarketclonecoding.notification.service.SseEmitterService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private SseEmitterService sseEmitterService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Nested
    @DisplayName("알림 추가 서비스 테스트")
    class AddNotification {

        @Test
        @DisplayName("성공")
        void addNotificationSuccess() {
            // given
            Long authId = 1111L;
            String content = "test";
            NotificationType type = NotificationType.LIKE;

            Member member = Member.builder().id(1L).authId(authId).build();
            when(memberRepository.findByAuthId(authId)).thenReturn(Optional.of(member));

            Notification notification = Notification.builder()
                    .member(member)
                    .content(content)
                    .type(type)
                    .isRead(false)
                    .build();
            when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

            // when
            notificationService.add(authId, type, content);

            // then
            verify(memberRepository).findByAuthId(authId);
            verify(notificationRepository).save(any(Notification.class));

            ArgumentCaptor<NotificationResponseDto> captor = forClass(NotificationResponseDto.class);
            verify(sseEmitterService).send(eq(authId), eq(NotificationType.LIKE), captor.capture());

            NotificationResponseDto capturedNotification = captor.getValue();
            assertThat(content).isEqualTo(capturedNotification.getContent());
            assertThat(type).isEqualTo(capturedNotification.getType());
            assertThat(capturedNotification.getIsRead()).isEqualTo(false);
        }

        @Test
        @DisplayName("실패 - 사용자가 존재하지 않음")
        void addNotificationFailMemberNotFound() {
            // given
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> notificationService.add(1111L, NotificationType.LIKE, "test"))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("사용자의 전체 알림 내용 조회 서비스 테스트")
    class GetAllNotifications {

        @Test
        @DisplayName("성공")
        void getAllNotificationsSuccess() {
            // given
            Long authId = 1111L;
            List<Notification> notifications = Arrays.asList(
                    Notification.builder().id(1L).build(),
                    Notification.builder().id(1L).build(),
                    Notification.builder().id(1L).build()
            );
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mock(Member.class)));
            when(notificationRepository.findAllByMember(any())).thenReturn(notifications);

            // when
            List<NotificationResponseDto> result = notificationService.getAllNotifications(authId);

            // then
            assertThat(result.size()).isEqualTo(3);
        }

        @Test
        @DisplayName("실패 - 사용자가 존재하지 않음")
        void getAllNotificationsFailMemberNotFound() {
            // given
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> notificationService.getAllNotifications(1111L))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("알림 읽음 처리 서비스 테스트")
    class Read {

        @Test
        @DisplayName("성공")
        void readTest() {
            // given
            Long authId = 1111L;
            Long id = 1L;
            Member mockMember = Member.builder()
                    .id(1L)
                    .authId(authId)
                    .build();
            Notification mockNotification = Notification.builder()
                    .id(id)
                    .member(mockMember)
                    .build();
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));
            when(notificationRepository.findById(anyLong())).thenReturn(Optional.of(mockNotification));

            // when
            notificationService.read(authId, id);

            // then
            assertThat(mockNotification.isRead()).isEqualTo(true);
        }

        @Test
        @DisplayName("실패 - 사용자가 존재하지 않음")
        void readFailMemberNotFound() {
            // given
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> notificationService.read(1111L, 1L))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("실패 - 알림이 존재하지 않음")
        void readFailNotificationNotFound() {
            // given
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mock(Member.class)));
            when(notificationRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> notificationService.read(1111L, 1L))
                    .isInstanceOf(NotificationNotExistsException.class)
                    .hasMessage(NOTIFICATION_NOT_EXISTS.getMessage());
        }

        @Test
        @DisplayName("실패 - 사용자의 알림이 아님")
        void readFailNotMemberOfNotification() {
            // given
            Member mockMember = Member.builder().id(1L).build();
            Member otherMember = Member.builder().id(2L).build();
            Notification mockNotification  = Notification.builder()
                    .id(1L)
                    .member(otherMember)
                    .build();
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));
            when(notificationRepository.findById(anyLong())).thenReturn(Optional.of(mockNotification));

            // when
            // then
            assertThatThrownBy(() -> notificationService.read(1111L, 1L))
                    .isInstanceOf(UnauthorizedAccessException.class)
                    .hasMessage(UNAUTHORIZED_ACCESS.getMessage());
        }
    }
}