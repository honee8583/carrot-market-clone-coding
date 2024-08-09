package com.carrot.carrotmarketclonecoding.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessMessage {
    BOARD_REGISTER_SUCCESS("게시글 작성에 성공하였습니다!"),
    BOARD_GET_DETAIL_SUCCESS("게시글 조회에 성공하였습니다!");

    private final String message;
}
