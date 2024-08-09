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
import org.springframework.data.redis.core.SetOperations;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataRedisTest
@ExtendWith(RedisContainerConfig.class)
class VisitServiceTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String BOARD_VISIT_KEY = "board:visit:";

    @Test
    @DisplayName("레디스 컨테이너를 사용해 게시판 set에 memberId value 저장 테스트")
    void testIncrementVisit() {
        // given
        String boardId = "1";
        String memberId = "1";
        String visitKey = BOARD_VISIT_KEY + boardId;
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();

        // when
        boolean isFirstTimeView =  setOperations.add(visitKey, memberId) > 0;
        redisTemplate.expire(visitKey, 1, TimeUnit.MINUTES);

        // then
        assertThat(isFirstTimeView).isEqualTo(true);
        assertThat(setOperations.pop(visitKey)).isEqualTo("1");
    }
}