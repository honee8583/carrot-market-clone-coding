package com.carrot.carrotmarketclonecoding.common.exception;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.MEMBER_ALREADY_LIKED_BOARD;

import org.springframework.http.HttpStatus;

public class MemberAlreadyLikedBoardException extends CustomException {
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getMessage() {
        return MEMBER_ALREADY_LIKED_BOARD.getMessage();
    }
}
