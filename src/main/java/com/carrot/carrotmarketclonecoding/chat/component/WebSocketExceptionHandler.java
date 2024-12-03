package com.carrot.carrotmarketclonecoding.chat.component;

import com.carrot.carrotmarketclonecoding.common.exception.CustomException;
import com.carrot.carrotmarketclonecoding.common.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class WebSocketExceptionHandler {

    @MessageExceptionHandler(CustomException.class)
    @SendToUser("/queue/errors")
    public ResponseResult handleCustomException(CustomException e) {
        log.debug("MessageExceptionHandler -> {}", e.getMessage());
        return ResponseResult.failed(e.getStatus(), e.getMessage(), null);
    }
}
