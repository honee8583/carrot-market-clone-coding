package com.carrot.carrotmarketclonecoding.common.exception;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.BOARD_NOT_FOUND;

import org.springframework.http.HttpStatus;

public class BoardNotFoundException extends CustomException
{
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getMessage() {
        return BOARD_NOT_FOUND.getMessage();
    }
}
