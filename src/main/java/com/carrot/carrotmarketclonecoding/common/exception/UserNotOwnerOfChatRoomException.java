package com.carrot.carrotmarketclonecoding.common.exception;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.FORBIDDEN;

import org.springframework.http.HttpStatus;

public class UserNotOwnerOfChatRoomException extends CustomException {

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.FORBIDDEN;
    }

    @Override
    public String getMessage() {
        return FORBIDDEN.getMessage();
    }
}
