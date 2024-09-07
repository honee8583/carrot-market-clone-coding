package com.carrot.carrotmarketclonecoding.common.exception;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.KAKAO_TOKEN_NOT_EXISTS;

import org.springframework.http.HttpStatus;

public class KakaoTokenNotExistsException extends CustomException {

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public String getMessage() {
        return KAKAO_TOKEN_NOT_EXISTS.getMessage();
    }
}
