package com.carrot.carrotmarketclonecoding.common.exception;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.UNAUTHORIZED_ACCESS;

import org.springframework.http.HttpStatus;

public class UnauthorizedAccessException extends CustomException {
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.FORBIDDEN;
    }

    @Override
    public String getMessage() {
        return UNAUTHORIZED_ACCESS.getMessage();
    }
}
