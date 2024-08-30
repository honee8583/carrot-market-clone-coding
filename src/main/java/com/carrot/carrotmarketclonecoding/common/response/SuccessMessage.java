package com.carrot.carrotmarketclonecoding.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessMessage {
    BOARD_REGISTER_SUCCESS("게시글 작성에 성공하였습니다!"),
    BOARD_GET_DETAIL_SUCCESS("게시글 조회에 성공하였습니다!"),
    BOARD_REGISTER_TEMPORARY_SUCCESS("게시글 임시저장에 성공하였습니다!"),
    BOARD_UPDATE_SUCCESS("게시글 수정에 성공하였습니다!"),
    BOARD_DELETE_SUCCESS("게시글 삭제에 성공하였습니다!"),
    BOARD_GET_TMP_SUCCESS("임시 게시글 조회에 성공하였습니다!"),
    ADD_BOARD_LIKE_SUCCESS("관심게시글 등록에 성공하였습니다!"),
    GET_MEMBER_LIKED_BOARDS_SUCCESS("관심 게시글 목록 조회에 성공하였습니다!"),
    SEARCH_BOARDS_SUCCESS("게시글 검색에 성공하였습니다!"),
    ADD_WORD_SUCCESS("자주쓰는문구 추가에 성공하였습니다!"),
    GET_MEMBER_WORDS("사용자의 자주쓰는문구 목록 조회에 성공하였습니다!"),
    UPDATE_WORD_SUCCESS("자주쓰는문구 수정에 성공하였습니다!"),
    REMOVE_WORD_SUCCESS("자주쓰는문구 삭제에 성공하였습니다!"),
    GET_TOP_RANK_SEARCH_KEYWORDS_SUCCESS("인기검색어 목록 조회에 성공하였습니다!"),
    GET_RECENT_SEARCH_KEYWORDS_SUCCESS("사용자의 최근 검색어 목록 조회에 성공하였습니다!"),
    REMOVE_RECENT_SEARCH_KEYWORD_SUCCESS("사용자의 최근 검색어 삭제에 성공하였습니다!"),
    REMOVE_ALL_RECENT_SEARCH_KEYWORD_SUCCESS("사용자의 최근 검색어 전체 삭제에 성공하였습니다!");

    private final String message;
}
