package com.carrot.carrotmarketclonecoding.common.exception;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.FILE_EXTENSION_NOT_VALID;

import org.springframework.http.HttpStatus;

public class FileExtensionNotValidException extends CustomException{

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNSUPPORTED_MEDIA_TYPE;
    }

    @Override
    public String getMessage() {
        return FILE_EXTENSION_NOT_VALID.getMessage();
    }
}
