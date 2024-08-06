package com.carrot.carrotmarketclonecoding.common.exception;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.FILE_NOT_EXISTS;

import org.springframework.http.HttpStatus;

public class FileNotExistsException extends CustomException {
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public String getMessage() {
        return FILE_NOT_EXISTS.getMessage();
    }
}
