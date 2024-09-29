package com.carrot.carrotmarketclonecoding.notification.dto;

import com.carrot.carrotmarketclonecoding.notification.domain.Notification;
import com.carrot.carrotmarketclonecoding.notification.domain.enums.NotificationType;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {
    private Long id;
    private String content;
    private NotificationType type;
    private Boolean isRead;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    public static NotificationResponseDto createNotificationResponseDto(Notification notification) {
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .type(notification.getType())
                .isRead(notification.isRead())
                .createDate(notification.getCreateDate())
                .updateDate(notification.getUpdateDate())
                .build();
    }
}
