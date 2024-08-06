package com.carrot.carrotmarketclonecoding.common.handler;

import com.carrot.carrotmarketclonecoding.common.exception.CustomException;
import com.carrot.carrotmarketclonecoding.common.response.FailedMessage;
import com.carrot.carrotmarketclonecoding.common.response.ResponseResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Component
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<?> customExceptionHandler(CustomException e) {
        return ResponseEntity.status(e.getStatus()).body(ResponseResult.failed(e.getStatus(), e.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<?> validationException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        Map<String, String> map = new HashMap<>();
        for (FieldError error: fieldErrors) {
            String field = error.getField();
            String message = error.getDefaultMessage();
            map.put(field, message);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseResult.failed(HttpStatus.BAD_REQUEST, FailedMessage.INPUT_NOT_VALID.getMessage(), map));
    }
}
