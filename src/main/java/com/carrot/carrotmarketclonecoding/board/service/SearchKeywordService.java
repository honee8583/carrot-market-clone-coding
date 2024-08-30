package com.carrot.carrotmarketclonecoding.board.service;

import java.util.List;
import java.util.Set;

public interface SearchKeywordService {
    void addSearchKeywordRank(String keyword);

    Set<String> getTopSearchKeywords();

    void addRecentSearchKeywords(Long memberId, String keyword);

    List<String> getRecentSearchKeywords(Long memberId);

    void removeRecentSearchKeyword(Long memberId, String keyword);

    void removeAllRecentSearchKeywords(Long memberId);
}
