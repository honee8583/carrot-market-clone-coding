package com.carrot.carrotmarketclonecoding.board.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.carrot.carrotmarketclonecoding.RedisContainerConfig;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataRedisTest
@ExtendWith(RedisContainerConfig.class)
class SearchKeywordServiceTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String SEARCH_RANK_KEY = "search:rank";
    private static final String SEARCH_RECENT_KEY = "search:recent:";
    private static final int MAX_RECENT_SEARCHES = 20;

    @AfterEach
    void rollBack() {
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Test
    @DisplayName("인기검색어 - 검색어 검색 횟수 증가")
    void addSearchKeywordsRank() {
        // given
        String keyword = "keyword";

        // when
        redisTemplate.opsForZSet().incrementScore(SEARCH_RANK_KEY, keyword, 1);
        Set<TypedTuple<String>> result = redisTemplate.opsForZSet().rangeWithScores(SEARCH_RANK_KEY, 0, -1);

        // then
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("인기검색어 - 인기검색어목록 조회")
    void getTopSearchKeywordsRank() {
        // given
        // when
        redisTemplate.opsForZSet().incrementScore(SEARCH_RANK_KEY, "keyboard", 1);
        redisTemplate.opsForZSet().incrementScore(SEARCH_RANK_KEY, "keyboard", 1);
        redisTemplate.opsForZSet().incrementScore(SEARCH_RANK_KEY, "mac", 1);
        redisTemplate.opsForZSet().incrementScore(SEARCH_RANK_KEY, "mac book", 1);

        Set<String> result = redisTemplate.opsForZSet().reverseRange(SEARCH_RANK_KEY, 0, 9);

        // then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("최근검색어 - 사용자의 최근검색어 추가")
    void addRecentSearchKeywords() {
        // given
        String key = SEARCH_RECENT_KEY + 1L;
        String keyword = "keyword";

        // when
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        listOperations.remove(key, 1, keyword);
        listOperations.rightPush(key, keyword);

        // then
        String result = listOperations.leftPop(key);
        assertThat(result).isEqualTo(keyword);
    }

    @Test
    @DisplayName("최근검색어 - 사용자의 최근검색어 개수가 20개가 넘어갈경우 오래된 검색어 제거")
    void addRecentSearchKeywordsRemoveOldKeyword() {
        // given
        Long memberId = 1L;
        String key = SEARCH_RECENT_KEY + memberId;
        String keyword = "keyword";

        // when
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        for (int i = 1; i <= 22; i++) {
            String keywordToSave = keyword + i;
            listOperations.remove(key, 1, keywordToSave);
            listOperations.rightPush(key, keywordToSave);
            if (listOperations.size(key) > MAX_RECENT_SEARCHES) {
                listOperations.leftPop(key);
            }
        }

        // then
        Long size = listOperations.size(key);
        String result = listOperations.leftPop(key);
        assertThat(size).isEqualTo(20);
        assertThat(result).isEqualTo("keyword3");
    }

    @Test
    @DisplayName("최근검색어 - 사용자의 최근검색어 목록 조회")
    void getRecentSearchKeywords() {
        // given
        Long memberId = 1L;
        String key = SEARCH_RECENT_KEY + memberId;

        // when
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        listOperations.rightPush(key, "keyword1");
        listOperations.rightPush(key, "keyword2");
        listOperations.rightPush(key, "keyword3");

        // then
        List<String> result = listOperations.range(key, 0, -1);
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("최근검색어 - 사용자의 특정 최근검색어 삭제")
    void removeRecentSearchKeyword() {
        // given
        Long memberId = 1L;
        String key = SEARCH_RECENT_KEY + memberId;

        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        listOperations.rightPush(key, "keyword1");
        listOperations.rightPush(key, "keyword2");
        listOperations.rightPush(key, "keyword3");

        // when
        listOperations.remove(key, 0, "keyword1");

        // then
        assertThat(listOperations.size(key)).isEqualTo(2);
        assertThat(listOperations.leftPop(key)).isEqualTo("keyword2");
        assertThat(listOperations.leftPop(key)).isEqualTo("keyword3");
    }

    @Test
    @DisplayName("최근검색어 - 전체 최근 검색어 삭제")
    void removeAllRecentSearchKeywords() {
        // given
        Long memberId = 1L;
        String key = SEARCH_RECENT_KEY + memberId;

        ListOperations<String, String> listOps = redisTemplate.opsForList();
        listOps.rightPush(key, "keyword1");
        listOps.rightPush(key, "keyword2");
        listOps.rightPush(key, "keyword3");

        // when
        redisTemplate.delete(key);

        // then
        Long size = listOps.size(key);
        assertThat(size).isEqualTo(0);
    }
}