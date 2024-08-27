package com.carrot.carrotmarketclonecoding.board.service;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SearchKeywordService {
    private final RedisTemplate<String, String> redisTemplate;

    private static final String SEARCH_RANK_KEY = "search:rank";
    private static final String SEARCH_RECENT_KEY = "search:recent:";
    private static final int MAX_RECENT_SEARCHES = 20;

    public void addSearchRank(String keyword) {
        redisTemplate.opsForZSet().incrementScore(SEARCH_RANK_KEY, keyword, 1);
    }

    public Set<String> getTopSearchRank() {
        return redisTemplate.opsForZSet().reverseRange(SEARCH_RANK_KEY, 0, 9);
    }

    public void addMemberSearchKeywords(Long memberId, String keyword) {
        String key = SEARCH_RECENT_KEY + memberId;

        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        listOperations.remove(key, 1, keyword);
        listOperations.rightPush(key, keyword);

        if (listOperations.size(key) > MAX_RECENT_SEARCHES) {
            listOperations.leftPop(key);
        }
    }

    public List<String> getRecentSearches(Long memberId) {
        String key = SEARCH_RECENT_KEY + memberId;
        return redisTemplate.opsForList().range(key, 0, -1);
    }
}
