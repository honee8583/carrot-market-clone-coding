package com.carrot.carrotmarketclonecoding.board.service;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VisitService {
    private final RedisTemplate<String, String> redisTemplate;

    private static final String BOARD_VISIT_KEY_PREFIX = "board:visit:";

    public boolean increaseVisit(String boardId, String memberId) {
        String key = BOARD_VISIT_KEY_PREFIX + boardId + ":" + memberId;
        Boolean memberViewed = redisTemplate.hasKey(key);
        if (memberViewed != null && !memberViewed) {
            redisTemplate.opsForValue().set(key, "1", 24, TimeUnit.HOURS);
            return true;
        }

        return false;
    }
}
