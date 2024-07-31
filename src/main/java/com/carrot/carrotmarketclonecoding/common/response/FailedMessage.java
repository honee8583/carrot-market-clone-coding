package com.carrot.carrotmarketclonecoding.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FailedMessage {
    MEMBER_NOT_FOUND("존재하지 않는 사용자입니다!"),
    CATEGORY_NOT_FOUND("존재하지 않는 카테고리입니다!"),
    INPUT_NOT_VALID("입력값이 잘못되었습니다!");

    private final String message;
}
