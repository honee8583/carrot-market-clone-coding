package com.carrot.carrotmarketclonecoding.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseResult {
    private int status;
    private Boolean result;
    private String message;
    private Object data;

    public static ResponseResult success(HttpStatus status, String message, Object data) {
        return new ResponseResult(status.value(), true, message, data);
    }

    public static ResponseResult failed(HttpStatus status, String message, Object data) {
        return new ResponseResult(status.value(), false, message, data);
    }
}
