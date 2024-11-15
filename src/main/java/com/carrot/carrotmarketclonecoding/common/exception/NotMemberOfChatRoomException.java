package com.carrot.carrotmarketclonecoding.common.exception;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.NOT_MEMBER_OF_CHAT_ROOM;

import org.springframework.http.HttpStatus;

public class NotMemberOfChatRoomException extends CustomException {

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.FORBIDDEN;
    }

    @Override
    public String getMessage() {
        return NOT_MEMBER_OF_CHAT_ROOM.getMessage();
    }
}
