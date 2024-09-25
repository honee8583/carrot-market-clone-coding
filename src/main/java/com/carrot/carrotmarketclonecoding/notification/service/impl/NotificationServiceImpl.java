package com.carrot.carrotmarketclonecoding.notification.service.impl;

import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import com.carrot.carrotmarketclonecoding.notification.domain.Notification;
import com.carrot.carrotmarketclonecoding.notification.domain.enums.NotificationType;
import com.carrot.carrotmarketclonecoding.notification.dto.NotificationResponseDto;
import com.carrot.carrotmarketclonecoding.notification.repository.NotificationRepository;
import com.carrot.carrotmarketclonecoding.notification.service.NotificationService;
import com.carrot.carrotmarketclonecoding.notification.service.SseEmitterService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final SseEmitterService sseEmitterService;

    @Override
    public void add(Long authId, NotificationType type, String content) {
        Member member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
        Notification notification = Notification.builder()
                .member(member)
                .content(content)
                .type(type)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
        sseEmitterService.send(authId, NotificationType.LIKE, content);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getAllNotifications(Long authId) {
        Member member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
        List<Notification> notifications = notificationRepository.findAllByMember(member);
        return notifications.stream().map(NotificationResponseDto::createNotificationResponseDto).toList();
    }
}
