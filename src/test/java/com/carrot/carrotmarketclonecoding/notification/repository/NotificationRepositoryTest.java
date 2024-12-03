package com.carrot.carrotmarketclonecoding.notification.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.carrot.carrotmarketclonecoding.RepositoryTest;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.notification.domain.Notification;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NotificationRepositoryTest extends RepositoryTest {

    @Test
    @DisplayName("findAllByMember 쿼리메서드 테스트")
    void findAllByMember() {
        // given
        Member member = Member.builder().build();
        memberRepository.save(member);

        List<Notification> notifications = Arrays.asList(
                Notification.builder().member(member).build(),
                Notification.builder().member(member).build(),
                Notification.builder().member(member).build()
        );
        notificationRepository.saveAll(notifications);

        // when
        List<Notification> result = notificationRepository.findAllByMember(member);

        // then
        assertThat(result.size()).isEqualTo(3);
    }
}