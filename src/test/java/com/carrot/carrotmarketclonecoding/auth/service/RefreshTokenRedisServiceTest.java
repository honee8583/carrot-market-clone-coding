package com.carrot.carrotmarketclonecoding.auth.service;

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
class RefreshTokenRedisServiceTest extends RedisTestContainerTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String REFRESH_TOKEN_KEY_PREFIX = "refreshToken:";
    private static final Long REFRESH_EXPIRATION_TIME = 1000L * 60 * 60L;

    @AfterEach
    void rollBack() {
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Test
    @DisplayName("리프레시 토큰 저장 및 조회")
    void saveRefreshToken() {
        // given
        Long authId = 1111L;
        String refreshToken = "refreshToken";

        // when
        redisTemplate.opsForValue().set(REFRESH_TOKEN_KEY_PREFIX + authId,
                refreshToken,
                REFRESH_EXPIRATION_TIME,
                TimeUnit.MILLISECONDS
                );

        // then
        String savedRefreshToken = redisTemplate.opsForValue().get(REFRESH_TOKEN_KEY_PREFIX + authId);
        assertThat(savedRefreshToken).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("리프레시 토큰 삭제")
    void getRefreshToken() {
        // given
        Long authId = 1111L;
        String refreshToken = "refreshToken";

        // when
        redisTemplate.opsForValue().set(REFRESH_TOKEN_KEY_PREFIX + authId,
                refreshToken,
                REFRESH_EXPIRATION_TIME,
                TimeUnit.MILLISECONDS
        );

        redisTemplate.delete(REFRESH_TOKEN_KEY_PREFIX + authId);

        // then
        String savedRefreshToken = redisTemplate.opsForValue().get(REFRESH_TOKEN_KEY_PREFIX + authId);
        assertThat(savedRefreshToken).isNull();
    }
}