package com.carrot.carrotmarketclonecoding.board;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SearchKeywordTestDisplayNames {
    SEARCH_KEYWORD_TOP_RANK_CONTROLLER_TEST(MESSAGE.SEARCH_KEYWORD_TOP_RANK_CONTROLLER_TEST),
    RECENT_SEARCH_KEYWORD_CONTROLLER_TEST(MESSAGE.RECENT_SEARCH_KEYWORD_CONTROLLER_TEST),
    SUCCESS(MESSAGE.SUCCESS);

    private final String message;

    public static class MESSAGE {
        public static final String SEARCH_KEYWORD_TOP_RANK_CONTROLLER_TEST = "인기검색어 목록 조회 컨트롤러 테스트";
        public static final String RECENT_SEARCH_KEYWORD_CONTROLLER_TEST = "사용자의 최근 검색어 목록 조회 컨트롤러 테스트";
        public static final String SUCCESS = "성공";
    }
}
