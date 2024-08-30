package com.carrot.carrotmarketclonecoding.board;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SearchKeywordTestDisplayNames {
    SEARCH_KEYWORD_TOP_RANK_CONTROLLER_TEST(MESSAGE.SEARCH_KEYWORD_TOP_RANK_CONTROLLER_TEST),
    RECENT_SEARCH_KEYWORD_CONTROLLER_TEST(MESSAGE.RECENT_SEARCH_KEYWORD_CONTROLLER_TEST),
    REMOVE_RECENT_SEARCH_KEYWORD_CONTROLLER_TEST(MESSAGE.REMOVE_RECENT_SEARCH_KEYWORD_CONTROLLER_TEST),
    REMOVE_ALL_RECENT_SEARCH_KEYWORDS_CONTROLLER_TEST(MESSAGE.REMOVE_ALL_RECENT_SEARCH_KEYWORDS_CONTROLLER_TEST),
    SUCCESS(MESSAGE.SUCCESS),
    FAIL_MEMBER_NOT_FOUND(MESSAGE.FAIL_MEMBER_NOT_FOUND);

    private final String message;

    public static class MESSAGE {
        public static final String SEARCH_KEYWORD_TOP_RANK_CONTROLLER_TEST = "인기검색어 목록 조회 컨트롤러 테스트";
        public static final String RECENT_SEARCH_KEYWORD_CONTROLLER_TEST = "사용자의 최근 검색어 목록 조회 컨트롤러 테스트";
        public static final String REMOVE_RECENT_SEARCH_KEYWORD_CONTROLLER_TEST = "사용자의 최근 검색어 삭제 컨트롤러 테스트";
        public static final String REMOVE_ALL_RECENT_SEARCH_KEYWORDS_CONTROLLER_TEST = "사용자의 최근 검색어 전체 삭제 컨트롤러 테스트";
        public static final String SUCCESS = "성공";
        public static final String FAIL_MEMBER_NOT_FOUND = "사용자가 존재하지 않음";
    }
}
