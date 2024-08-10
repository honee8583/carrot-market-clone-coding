package com.carrot.carrotmarketclonecoding.board.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.carrot.carrotmarketclonecoding.RedisContainerConfig;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataRedisTest
@ExtendWith(RedisContainerConfig.class)
class VisitServiceTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String BOARD_VISIT_KEY = "board:visit:";

    @Test
    @DisplayName("레디스 TestContainer를 사용해 조회이력 저장 테스트")
    void testIncrementVisit() {
        // given
        String boardId = "1";
        String memberId = "1";
        String key = BOARD_VISIT_KEY + boardId + ":" + memberId;
        redisTemplate.opsForValue().set(key, "1", 24, TimeUnit.HOURS);

        // when
        Boolean memberViewed = redisTemplate.hasKey(key);

        // then
        assertThat(memberViewed).isEqualTo(true);
        assertThat(redisTemplate.opsForValue().get(key)).isEqualTo("1");
    }
}