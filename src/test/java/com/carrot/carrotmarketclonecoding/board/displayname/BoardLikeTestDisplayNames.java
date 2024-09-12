package com.carrot.carrotmarketclonecoding.board.displayname;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BoardLikeTestDisplayNames {
    SUCCESS(MESSAGE.SUCCESS),
    ADD_BOARD_LIKE_CONTROLLER_TEST(MESSAGE.ADD_BOARD_LIKE_CONTROLLER_TEST),
    GET_BOARD_LIKE_LIST_CONTROLLER_TEST(MESSAGE.GET_BOARD_LIKE_LIST_CONTROLLER_TEST),
    FAIL_BOARD_NOT_FOUND(MESSAGE.FAIL_BOARD_NOT_FOUND),
    FAIL_MEMBER_NOT_FOUND(MESSAGE.FAIL_MEMBER_NOT_FOUND),
    FAIL_BOARD_LIKE_ALREADY_ADDED(MESSAGE.FAIL_BOARD_LIKE_ALREADY_ADDED);

    private final String message;

    public static class MESSAGE {
        public static final String ADD_BOARD_LIKE_CONTROLLER_TEST = "관심게시글 등록 컨트롤러 테스트";
        public static final String GET_BOARD_LIKE_LIST_CONTROLLER_TEST = "관심게시글 목록 조회 컨트롤러 테스트";

        public static final String FAIL_BOARD_NOT_FOUND = "실패 - 게시글이 존재하지 않음";
        public static final String FAIL_MEMBER_NOT_FOUND = "실패 - 사용자가 존재하지 않음";
        public static final String FAIL_BOARD_LIKE_ALREADY_ADDED = "실패 - 사용자가 이미 관심게시글로 등록한 게시글임";
        public static final String SUCCESS = "성공";
    }
}
