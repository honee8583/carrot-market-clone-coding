package com.carrot.carrotmarketclonecoding.common.exception;

import com.carrot.carrotmarketclonecoding.common.response.FailedMessage;
import org.springframework.http.HttpStatus;

public class KeywordNotFoundException extends CustomException {

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getMessage() {
        return FailedMessage.KEYWORD_NOT_FOUND.getMessage();
    }
}
