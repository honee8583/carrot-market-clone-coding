package com.carrot.carrotmarketclonecoding.board.service;

import java.util.List;
import java.util.Set;

public interface SearchKeywordService {
    Set<String> getTopSearchKeywords();
    List<String> getRecentSearchKeywords(Long authId);
    void removeRecentSearchKeyword(Long authId, String keyword);
    void removeAllRecentSearchKeywords(Long authId);
}
