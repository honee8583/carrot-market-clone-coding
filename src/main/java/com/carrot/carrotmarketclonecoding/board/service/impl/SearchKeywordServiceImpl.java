package com.carrot.carrotmarketclonecoding.board.service.impl;

import com.carrot.carrotmarketclonecoding.board.service.SearchKeywordService;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchKeywordServiceImpl implements SearchKeywordService {
    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRepository memberRepository;

    private static final String SEARCH_RANK_KEY = "search:rank";
    private static final String SEARCH_RECENT_KEY = "search:recent:";
    private static final int MAX_RECENT_SEARCHES = 20;

    @Override
    public void addSearchKeywordRank(String keyword) {
        redisTemplate.opsForZSet().incrementScore(SEARCH_RANK_KEY, keyword, 1);
    }

    @Override
    public Set<String> getTopSearchKeywords() {
        return redisTemplate.opsForZSet().reverseRange(SEARCH_RANK_KEY, 0, 9);
    }

    @Override
    public void addRecentSearchKeywords(Long memberId, String keyword) {
        isMemberExist(memberId);
        String key = SEARCH_RECENT_KEY + memberId;

        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        listOperations.remove(key, 1, keyword);
        listOperations.rightPush(key, keyword);

        if (listOperations.size(key) > MAX_RECENT_SEARCHES) {
            listOperations.leftPop(key);
        }
    }

    @Override
    public List<String> getRecentSearchKeywords(Long memberId) {
        isMemberExist(memberId);
        String key = SEARCH_RECENT_KEY + memberId;
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    @Override
    public void removeRecentSearchKeyword(Long memberId, String keyword) {
        isMemberExist(memberId);
        String key = SEARCH_RECENT_KEY + memberId;
        redisTemplate.opsForList().remove(key, 0, keyword);
    }

    @Override
    public void removeAllRecentSearchKeywords(Long memberId) {
        isMemberExist(memberId);
        redisTemplate.delete(SEARCH_RECENT_KEY + memberId);
    }

    private void isMemberExist(Long memberId) {
        memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
    }
}
