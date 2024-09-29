package com.carrot.carrotmarketclonecoding.notification.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    @DisplayName("알림 추가 성공")
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
    @DisplayName("이전 알림 내용 전체 조회 성공 테스트")
    void getAllNotificationsTest() {
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
}