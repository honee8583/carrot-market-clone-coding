package com.carrot.carrotmarketclonecoding.common.exception;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.MEMBER_WORD_OVER_LIMIT;

import org.springframework.http.HttpStatus;

public class MemberWordLimitException extends CustomException {

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getMessage() {
        return MEMBER_WORD_OVER_LIMIT.getMessage();
    }
}
