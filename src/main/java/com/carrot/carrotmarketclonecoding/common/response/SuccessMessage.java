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
    REMOVE_ALL_RECENT_SEARCH_KEYWORD_SUCCESS("사용자의 최근 검색어 전체 삭제에 성공하였습니다!"),

    LOGIN_SUCCESS("로그인에 성공하였습니다!"),
    RECREATE_TOKENS_SUCCESS("토큰 재발급에 성공하였습니다!"),
    LOGOUT_SUCCESS("로그아웃에 성공하였습니다!"),
    WITHDRAW_SUCCESS("회원탈퇴에 성공하였습니다!"),

    PROFILE_UPDATE_SUCCESS("프로필 수정에 성공하였습니다!"),
    PROFILE_DETAIL_SUCCESS("프로필 정보 조회에 성공하였습니다!"),

    GET_CATEGORIES_SUCCESS("카테고리 전체 목록 조회에 성공하였습니다!"),

    GET_ALL_NOTIFICATIONS_SUCCESS("이전 알림 조회에 성공하였습니다!"),
    READ_NOTIFICATION_SUCCESS("알림 읽음처리에 성공하였습니다!"),

    ADD_KEYWORD_SUCCESS("키워드 추가에 성공하였습니다!"),
    EDIT_KEYWORD_SUCCESS("키워드 편집에 성공하였습니다!"),
    GET_KEYWORDS_SUCCESS("사용자의 키워드 목록 조회에 성공하였습니다!"),
    DELETE_KEYWORDS_SUCCESS("키워드 삭제에 성공하였습니다!"),

    CREATE_CHAT_ROOM_SUCCESS("채팅방 개설에 성공하였습니다!"),
    GET_CHAT_ROOMS_SUCCESS("채팅방 목록 조회에 성공하였습니다!"),
    DELETE_CHAT_ROOM_SUCCESS("채팅방 삭제에 성공하였습니다!"),
    GET_CHAT_MESSAGES_SUCCESS("채팅 내역 조회에 성공하였습니다!");

    private final String message;
}
