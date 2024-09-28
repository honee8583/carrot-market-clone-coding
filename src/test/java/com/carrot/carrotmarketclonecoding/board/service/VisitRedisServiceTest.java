package com.carrot.carrotmarketclonecoding.board.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.carrot.carrotmarketclonecoding.RedisTestContainerTest;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class VisitRedisServiceTest extends RedisTestContainerTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String BOARD_VISIT_KEY = "board:visit:";

    @AfterEach
    void tearDown() {
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

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