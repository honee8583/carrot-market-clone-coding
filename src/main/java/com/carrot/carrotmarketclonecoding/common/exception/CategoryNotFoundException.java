package com.carrot.carrotmarketclonecoding.common.exception;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.CATEGORY_NOT_FOUND;

import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends CustomException {

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getMessage() {
        return CATEGORY_NOT_FOUND.getMessage();
    }
}
