package com.carrot.carrotmarketclonecoding.board.service;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VisitService {
    private final RedisTemplate<String, String> redisTemplate;

    private static final String BOARD_VISIT_KEY = "board:visit:";

    public boolean increaseVisit(String boardId, String memberId) {
        String visitKey = BOARD_VISIT_KEY + boardId;
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        boolean isFirstTimeView =  setOps.add(visitKey, memberId) > 0;
        redisTemplate.expire(visitKey, 1, TimeUnit.DAYS);

        return isFirstTimeView;
    }
}
