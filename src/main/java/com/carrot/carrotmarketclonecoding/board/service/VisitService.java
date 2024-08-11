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

    public boolean increaseVisit(String boardId, String sessionId) {
        String sessionKey = BOARD_VISIT_KEY_PREFIX + boardId + ":session:" + sessionId;
        Boolean viewed = redisTemplate.hasKey(sessionKey);
        if (sessionId != null && sessionId.length() > 0) {
            return setSessionKey(viewed, sessionKey);
        }
        return false;
    }

    private boolean setSessionKey(Boolean viewed, String sessionKey) {
        if (viewed != null && !viewed) {
            redisTemplate.opsForValue().set(sessionKey, "1", 24, TimeUnit.HOURS);
            return true;
        }
        return false;
    }
}
