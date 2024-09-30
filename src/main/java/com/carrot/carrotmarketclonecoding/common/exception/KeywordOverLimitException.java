package com.carrot.carrotmarketclonecoding.common.exception;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.KEYWORD_OVER_LIMIT;

import org.springframework.http.HttpStatus;

public class KeywordOverLimitException extends CustomException {

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getMessage() {
        return KEYWORD_OVER_LIMIT.getMessage();
    }
}
