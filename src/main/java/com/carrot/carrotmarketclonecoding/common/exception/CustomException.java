package com.carrot.carrotmarketclonecoding.common.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

public abstract class CustomException extends RuntimeException {
    abstract public HttpStatus getStatus();
    abstract public String getMessage();
}
