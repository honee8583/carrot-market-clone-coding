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
    MEMBER_WORD_OVER_LIMIT("자주쓰는문구의 개수는 30개를 초과할 수 없습니다!");

    private final String message;
}
