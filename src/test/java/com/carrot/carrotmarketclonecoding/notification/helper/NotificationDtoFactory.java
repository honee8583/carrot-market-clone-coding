package com.carrot.carrotmarketclonecoding.notification.helper;

import com.carrot.carrotmarketclonecoding.notification.domain.enums.NotificationType;
import com.carrot.carrotmarketclonecoding.notification.dto.NotificationResponseDto;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class NotificationDtoFactory {

    public List<NotificationResponseDto> createNotifications() {
        return Arrays.asList(
                NotificationResponseDto.builder()
                        .id(1L)
                        .content("notification1")
                        .type(NotificationType.LIKE)
                        .isRead(false)
                        .createDate(LocalDateTime.now())
                        .updateDate(LocalDateTime.now())
                        .build(),
                NotificationResponseDto.builder()
                        .id(2L)
                        .content("notification2")
                        .type(NotificationType.CONNECT)
                        .isRead(true)
                        .createDate(LocalDateTime.now())
                        .updateDate(LocalDateTime.now())
                        .build()
        );
    }
}
