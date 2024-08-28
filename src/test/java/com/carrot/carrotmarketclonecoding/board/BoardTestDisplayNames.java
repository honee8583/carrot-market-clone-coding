package com.carrot.carrotmarketclonecoding.board;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BoardTestDisplayNames {
    BOARD_REGISTER_SERVICE_TEST(MESSAGE.BOARD_REGISTER_SERVICE_TEST),
    BOARD_DETAIL_SERVICE_TEST(MESSAGE.BOARD_DETAIL_SERVICE_TEST),
    BOARD_SEARCH_SERVICE_TEST(MESSAGE.BOARD_SEARCH_SERVICE_TEST),
    BOARD_MY_DETAIL_SERVICE_TEST(MESSAGE.BOARD_MY_DETAIL_SERVICE_TEST),
    BOARD_UPDATE_SERVICE_TEST(MESSAGE.BOARD_UPDATE_SERVICE_TEST),
    BOARD_DELETE_SERVICE_TEST(MESSAGE.BOARD_DELETE_SERVICE_TEST),
    BOARD_GET_TMP_SERVICE_TEST(MESSAGE.BOARD_GET_TMP_SERVICE_TEST),
    BOARD_REGISTER_CONTROLLER_TEST(MESSAGE.BOARD_REGISTER_CONTROLLER_TEST),
    BOARD_DETAIL_CONTROLLER_TEST(MESSAGE.BOARD_DETAIL_CONTROLLER_TEST),
    BOARD_SEARCH_CONTROLLER_TEST(MESSAGE.BOARD_SEARCH_CONTROLLER_TEST),
    BOARD_MY_DETAIL_CONTROLLER_TEST(MESSAGE.BOARD_MY_DETAIL_CONTROLLER_TEST),
    BOARD_UPDATE_CONTROLLER_TEST(MESSAGE.BOARD_UPDATE_CONTROLLER_TEST),
    BOARD_DELETE_CONTROLLER_TEST(MESSAGE.BOARD_DELETE_CONTROLLER_TEST),
    BOARD_GET_TMP_CONTROLLER_TEST(MESSAGE.BOARD_GET_TMP_CONTROLLER_TEST),
    SUCCESS(MESSAGE.SUCCESS),
    SUCCESS_REGISTER_TMP_BOARD(MESSAGE.SUCCESS_REGISTER_TMP_BOARD),
    SUCCESS_INCLUDE_INCREASE_VISIT(MESSAGE.SUCCESS_INCLUDE_INCREASE_VISIT),
    SUCCESS_NOT_INCREASE_VISIT_REVISIT_IN_24HOURS(MESSAGE.SUCCESS_NOT_INCREASE_VISIT_REVISIT_IN_24HOURS),
    SUCCESS_DELETE_OLD_TMP_BOARDS(MESSAGE.SUCCESS_DELETE_OLD_TMP_BOARDS),
    SUCCESS_NOT_DELETE_OLD_TMP_BOARDS_IF_BOARD_NOT_TMP(MESSAGE.SUCCESS_NOT_DELETE_OLD_TMP_BOARDS_IF_BOARD_NOT_TMP),
    SUCCESS_NO_TMP_BOARDS(MESSAGE.SUCCESS_NO_TMP_BOARDS),
    FAIL_INPUT_NOT_VALID(MESSAGE.FAIL_INPUT_NOT_VALID),
    FAIL_FILE_COUNT_OVER_10(MESSAGE.FAIL_FILE_COUNT_OVER_10),
    FAIL_WRITER_NOT_FOUND(MESSAGE.FAIL_WRITER_NOT_FOUND),
    FAIL_MEMBER_NOT_FOUND(MESSAGE.FAIL_MEMBER_NOT_FOUND),
    FAIL_CATEGORY_NOT_FOUND(MESSAGE.FAIL_CATEGORY_NOT_FOUND),
    FAIL_BOARD_NOT_FOUND(MESSAGE.FAIL_BOARD_NOT_FOUND),
    FAIL_MEMBER_IS_NOT_WRITER(MESSAGE.FAIL_MEMBER_IS_NOT_WRITER),
    FAIL_NEW_PICTURES_COUNT_OVER_10(MESSAGE.FAIL_NEW_PICTURES_COUNT_OVER_10);

    private final String message;

    public static class MESSAGE {
        public static final String BOARD_REGISTER_SERVICE_TEST = "게시글 작성 서비스 테스트";
        public static final String BOARD_DETAIL_SERVICE_TEST = "게시글 조회 서비스 테스트";
        public static final String BOARD_SEARCH_SERVICE_TEST = "게시글 검색 서비스 테스트";
        public static final String BOARD_MY_DETAIL_SERVICE_TEST = "내 게시글 조회 서비스 테스트";
        public static final String BOARD_UPDATE_SERVICE_TEST = "게시글 수정 서비스 테스트";
        public static final String BOARD_DELETE_SERVICE_TEST = "게시글 삭제 서비스 테스트";
        public static final String BOARD_GET_TMP_SERVICE_TEST = "임시 게시글 조회 서비스 테스트";

        public static final String BOARD_REGISTER_CONTROLLER_TEST = "게시글 작성 컨트롤러 테스트";
        public static final String BOARD_DETAIL_CONTROLLER_TEST = "게시글 조회 컨트롤러 테스트";
        public static final String BOARD_SEARCH_CONTROLLER_TEST = "게시글 검색 컨트롤러 테스트";
        public static final String BOARD_MY_DETAIL_CONTROLLER_TEST = "내 게시글 조회 컨트롤러 테스트";
        public static final String BOARD_UPDATE_CONTROLLER_TEST = "게시글 수정 컨트롤러 테스트";
        public static final String BOARD_DELETE_CONTROLLER_TEST = "게시글 삭제 컨트롤러 테스트";
        public static final String BOARD_GET_TMP_CONTROLLER_TEST = "임시저장된 게시글 조회 컨트롤러 테스트";

        public static final String SUCCESS = "성공";
        public static final String SUCCESS_INCLUDE_INCREASE_VISIT = "성공 - 조회수 증가 포함";
        public static final String SUCCESS_NOT_INCREASE_VISIT_REVISIT_IN_24HOURS = "성공 - 24시간내에 재조회할경우 조회수 증가 x";
        public static final String SUCCESS_DELETE_OLD_TMP_BOARDS = "성공 - 임시저장한 게시글을 수정한경우 이전 임시저장게시글 모두 삭제";
        public static final String SUCCESS_NOT_DELETE_OLD_TMP_BOARDS_IF_BOARD_NOT_TMP = "성공 - 임시저장한 게시글이 아닐경우 이전의 임시저장한 게시글을 삭제x";
        public static final String SUCCESS_NO_TMP_BOARDS = "성공 - 임싯저장 게시글이 존재하지 않음";
        public static final String SUCCESS_REGISTER_TMP_BOARD = "성공 - 임시게시글 저장";
        public static final String FAIL_INPUT_NOT_VALID = "실패 - 유효성 검사 실패";
        public static final String FAIL_FILE_COUNT_OVER_10 = "실패 - 업로드 요청한 파일의 개수가 10개 초과";
        public static final String FAIL_WRITER_NOT_FOUND = "실패 - 존재하지 않는 작성자";
        public static final String FAIL_MEMBER_NOT_FOUND = "실패 - 존재하지 않는 사용자";
        public static final String FAIL_CATEGORY_NOT_FOUND = "실패 - 존재하지 않는 카테고리";
        public static final String FAIL_BOARD_NOT_FOUND = "실패 - 존재하지 않는 게시판";
        public static final String FAIL_MEMBER_IS_NOT_WRITER = "실패 - 작성자와 사용자가 일치하지 않음";
        public static final String FAIL_NEW_PICTURES_COUNT_OVER_10 = "실패 - 새로 첨부하는 사진의 개수가 10개를 넘음";
    }
}
