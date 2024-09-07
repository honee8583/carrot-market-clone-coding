package com.carrot.carrotmarketclonecoding.auth.service;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenRedisService {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.refresh_expiration_time}")
    private Long REFRESH_EXPIRATION_TIME;

    private static final String REFRESH_TOKEN_KEY_PREFIX = "refreshToken:";

    public void saveRefreshToken(Long authId, String refreshToken) {
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_KEY_PREFIX + authId,
                refreshToken,
                REFRESH_EXPIRATION_TIME,
                TimeUnit.MILLISECONDS
        );
    }

    public String getRefreshToken(Long authId) {
        return redisTemplate.opsForValue().get(REFRESH_TOKEN_KEY_PREFIX + authId);
    }

    public void deleteRefreshToken(Long authId) {
        redisTemplate.delete(REFRESH_TOKEN_KEY_PREFIX + authId);
    }
}
