package com.carrot.carrotmarketclonecoding.notification.domain;

import com.carrot.carrotmarketclonecoding.common.BaseEntity;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.notification.domain.enums.NotificationType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private String content;

    private boolean isRead;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    public void readNotification() {
        this.isRead = true;
    }
}
