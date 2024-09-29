package com.carrot.carrotmarketclonecoding.common.exception;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.NOTIFICATION_NOT_EXISTS;

import org.springframework.http.HttpStatus;

public class NotificationNotExistsException extends CustomException {

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getMessage() {
        return NOTIFICATION_NOT_EXISTS.getMessage();
    }
}
