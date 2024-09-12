package com.carrot.carrotmarketclonecoding.word.displayname;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WordTestDisplayNames {
    WORD_REMOVE_CONTROLLER_TEST(MESSAGE.WORD_REMOVE_CONTROLLER_TEST),
    WORD_UPDATE_CONTROLLER_TEST(MESSAGE.WORD_UPDATE_CONTROLLER_TEST),
    WORD_LIST_CONTROLLER_TEST(MESSAGE.WORD_LIST_CONTROLLER_TEST),
    WORD_ADD_CONTROLLER_TEST(MESSAGE.WORD_ADD_CONTROLLER_TEST),
    WORD_REMOVE_SERVICE_TEST(MESSAGE.WORD_REMOVE_SERVICE_TEST),
    WORD_UPDATE_SERVICE_TEST(MESSAGE.WORD_UPDATE_SERVICE_TEST),
    WORD_LIST_SERVICE_TEST(MESSAGE.WORD_LIST_SERVICE_TEST),
    WORD_ADD_SERVICE_TEST(MESSAGE.WORD_ADD_SERVICE_TEST),
    SUCCESS(MESSAGE.SUCCESS),
    FAIL_INPUT_NOT_VALID(MESSAGE.FAIL_INPUT_NOT_VALID),
    FAIL_MEMBER_NOT_FOUND(MESSAGE.FAIL_MEMBER_NOT_FOUND),
    FAIL_WORD_NOT_FOUND(MESSAGE.FAIL_WORD_NOT_FOUND),
    FAIL_MEMBER_WORD_OVER_LIMIT(MESSAGE.FAIL_MEMBER_WORD_OVER_LIMIT);

    private final String message;

    public static class MESSAGE {
        public static final String WORD_REMOVE_CONTROLLER_TEST = "자주쓰는문구 삭제 컨트롤러 테스트";
        public static final String WORD_UPDATE_CONTROLLER_TEST = "자주쓰는문구 수정 컨트롤러 테스트";
        public static final String WORD_LIST_CONTROLLER_TEST = "자주쓰는문구 목록 조회 컨트롤러 테스트";
        public static final String WORD_ADD_CONTROLLER_TEST = "자주쓰는문구 추가 컨트롤러 테스트";

        public static final String WORD_REMOVE_SERVICE_TEST = "자주쓰는문구 삭제 서비스 테스트";
        public static final String WORD_UPDATE_SERVICE_TEST = "자주쓰는문구 수정 서비스 테스트";
        public static final String WORD_LIST_SERVICE_TEST = "자주쓰는문구 목록 조회 서비스 테스트";
        public static final String WORD_ADD_SERVICE_TEST = "자주쓰는문구 추가 서비스 테스트";

        public static final String SUCCESS = "성공";
        public static final String FAIL_INPUT_NOT_VALID = "실패 - 유효성 검사 실패";
        public static final String FAIL_MEMBER_NOT_FOUND = "실패 - 사용자 존재하지 않음";
        public static final String FAIL_WORD_NOT_FOUND = "실패 - 자주쓰는문구 존재하지 않음";
        public static final String FAIL_MEMBER_WORD_OVER_LIMIT = "실패 - 자주쓰는문구의 개수가 30개를 초과함";
    }
}
