package com.carrot.carrotmarketclonecoding.common.exception;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.CHAT_ROOM_NOT_FOUND;

import org.springframework.http.HttpStatus;

public class ChatRoomNotFoundException extends CustomException {

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getMessage() {
        return CHAT_ROOM_NOT_FOUND.getMessage();
    }
}
