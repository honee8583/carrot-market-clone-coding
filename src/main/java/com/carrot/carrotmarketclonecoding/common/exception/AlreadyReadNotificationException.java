package com.carrot.carrotmarketclonecoding.common.exception;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.ALREADY_READ_NOTIFICATION;

import org.springframework.http.HttpStatus;

public class AlreadyReadNotificationException extends CustomException {

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getMessage() {
        return ALREADY_READ_NOTIFICATION.getMessage();
    }
}
