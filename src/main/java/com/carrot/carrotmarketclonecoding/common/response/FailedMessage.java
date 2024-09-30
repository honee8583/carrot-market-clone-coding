package com.carrot.carrotmarketclonecoding.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FailedMessage {
    MEMBER_NOT_FOUND("존재하지 않는 사용자입니다!"),
    CATEGORY_NOT_FOUND("존재하지 않는 카테고리입니다!"),
    BOARD_NOT_FOUND("존재하지 않는 게시글입니다!"),
    INPUT_NOT_VALID("입력값이 잘못되었습니다!"),
    FILE_EXTENSION_NOT_VALID("png 혹은 jpeg/jpg 파일이 아닙니다!"),
    FILE_UPLOAD_FAILED("파일 업로드에 실패하였습니다!"),
    FILE_NOT_EXISTS("파일이 존재하지 않습니다!"),
    FILE_UPLOAD_LIMIT("업로드 가능한 파일은 10개까지입니다!"),
    UNAUTHORIZED_ACCESS("접근권한을 가지고 있지 않습니다!"),

    MEMBER_ALREADY_LIKED_BOARD("이미 관심게시글로 등록한 게시글입니다!"),
    MEMBER_WORD_OVER_LIMIT("자주쓰는문구의 개수는 30개를 초과할 수 없습니다!"),
    WORD_NOT_FOUND("존재하지 않는 자주쓰는문구입니다!"),

    TOKEN_EXPIRED_MESSAGE("토큰이 만료되었습니다!"),
    TOKEN_NOT_VALID("잘못된 토큰입니다!"),
    FORBIDDEN("접근권한이 없습니다!"),
    ACCESS_DENIED("로그인을 시도해주세요!"),
    TOKEN_NOT_EXISTS("토큰이 존재하지 않습니다!"),
    KAKAO_TOKEN_NOT_EXISTS("카카오로부터 받아온 토큰이 존재하지 않습니다!"),
    KAKAO_USER_INFO_NOT_EXISTS("카카오로부터 받아온 사용자 정보가 존재하지 않습니다!"),
    REFRESH_TOKEN_NOT_MATCH("리프레시 토큰이 일치하지 않습니다!"),

    NOTIFICATION_NOT_EXISTS("존재하지 않는 알림입니다!"),
    ALREADY_READ_NOTIFICATION("이미 읽은 알림입니다!"),

    KEYWORD_OVER_LIMIT("키워드개수는 30개까지만 저장할 수 있습니다!");

    private final String message;
}
