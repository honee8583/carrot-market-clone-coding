package com.carrot.carrotmarketclonecoding.common.exception;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.REFRESH_TOKEN_NOT_MATCH;

import org.springframework.http.HttpStatus;

public class RefreshTokenNotMatchException extends CustomException {

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

    @Override
    public String getMessage() {
        return REFRESH_TOKEN_NOT_MATCH.getMessage();
    }
}
