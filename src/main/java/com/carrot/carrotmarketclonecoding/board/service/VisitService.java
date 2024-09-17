package com.carrot.carrotmarketclonecoding.board.service;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VisitService {
    private final RedisTemplate<String, String> redisTemplate;

    private static final String BOARD_VISIT_KEY_PREFIX = "viewed:";

    public boolean increaseVisit(String boardId, String ip, String userAgent) {
        String key = BOARD_VISIT_KEY_PREFIX + boardId + ":" + ip + ":" + userAgent;
        Boolean viewed = redisTemplate.hasKey(key);
        if (ip != null && ip.length() > 0 && userAgent != null && userAgent.length() > 0) {
            return setVisitData(viewed, key);
        }
        return false;
    }

    private boolean setVisitData(Boolean viewed, String key) {
        if (viewed != null && !viewed) {
            redisTemplate.opsForValue().set(key, "1", 24, TimeUnit.HOURS);
            return true;
        }
        return false;
    }
}
