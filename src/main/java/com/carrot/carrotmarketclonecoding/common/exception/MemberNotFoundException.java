package com.carrot.carrotmarketclonecoding.common.exception;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.MEMBER_NOT_FOUND;

import org.springframework.http.HttpStatus;

public class MemberNotFoundException extends CustomException {

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

    @Override
    public String getMessage() {
        return MEMBER_NOT_FOUND.getMessage();
    }
}
