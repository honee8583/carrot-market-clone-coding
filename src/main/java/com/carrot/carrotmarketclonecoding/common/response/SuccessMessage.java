package com.carrot.carrotmarketclonecoding.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessMessage {
    BOARD_REGISTER_SUCCESS("게시글 작성에 성공하였습니다!"),
    BOARD_GET_DETAIL_SUCCESS("게시글 조회에 성공하였습니다!"),
    BOARD_REGISTER_TEMPORARY_SUCCESS("게시글 임시저장에 성공하였습니다!"),
    BOARD_UPDATE_SUCCESS("게시글 수정에 성공하였습니다!");

    private final String message;
}
