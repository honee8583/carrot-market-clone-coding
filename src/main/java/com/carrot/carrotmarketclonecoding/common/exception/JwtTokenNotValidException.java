package com.carrot.carrotmarketclonecoding.common.exception;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.TOKEN_NOT_VALID;

import org.springframework.http.HttpStatus;

public class JwtTokenNotValidException extends CustomException {

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

    @Override
    public String getMessage() {
        return TOKEN_NOT_VALID.getMessage();
    }
}
