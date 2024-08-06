package com.carrot.carrotmarketclonecoding.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FailedMessage {
    MEMBER_NOT_FOUND("존재하지 않는 사용자입니다!"),
    CATEGORY_NOT_FOUND("존재하지 않는 카테고리입니다!"),
    INPUT_NOT_VALID("입력값이 잘못되었습니다!"),
    FILE_EXTENSION_NOT_VALID("png 혹은 jpeg/jpg 파일이 아닙니다!"),
    FILE_UPLOAD_FAILED("파일 업로드에 실패하였습니다!"),
    FILE_NOT_EXISTS("파일이 존재하지 않습니다!");

    private final String message;
}
