package com.carrot.carrotmarketclonecoding.board.service.impl;

import com.carrot.carrotmarketclonecoding.board.service.SearchKeywordRedisService;
import com.carrot.carrotmarketclonecoding.board.service.SearchKeywordService;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchKeywordServiceImpl implements SearchKeywordService {
    private final MemberRepository memberRepository;
    private final SearchKeywordRedisService searchKeywordRedisService;

    @Override
    public Set<String> getTopSearchKeywords() {
        return searchKeywordRedisService.getTopSearchKeywords();
    }

    @Override
    public List<String> getRecentSearchKeywords(Long authId) {
        Member member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
        return searchKeywordRedisService.getRecentSearchKeywords(member.getId());
    }

    @Override
    public void removeRecentSearchKeyword(Long authId, String keyword) {
        Member member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
        searchKeywordRedisService.removeRecentSearchKeyword(member.getId(), keyword);
    }

    @Override
    public void removeAllRecentSearchKeywords(Long authId) {
        Member member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
        searchKeywordRedisService.removeAllRecentSearchKeywords(member.getId());
    }
}
