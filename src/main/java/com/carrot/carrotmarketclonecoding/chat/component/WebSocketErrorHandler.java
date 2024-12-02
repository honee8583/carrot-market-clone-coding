package com.carrot.carrotmarketclonecoding.chat.component;

import com.carrot.carrotmarketclonecoding.common.exception.CustomException;
import com.carrot.carrotmarketclonecoding.common.response.ResponseResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Slf4j
@Component
public class WebSocketErrorHandler extends StompSubProtocolErrorHandler {

    @Override
    @SendToUser("/queue/errors")
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        log.debug("Error Handler...");

        Throwable exception = parseException(ex);

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setMessage(exception.getMessage());
        accessor.setLeaveMutable(true);

        log.debug(exception.getMessage());

        if (exception instanceof CustomException customException) {
            return sendCustomExceptionMessage(customException, accessor);
        }

        return sendServerErrorMessage(accessor, exception.getMessage());
    }

    private Message<byte[]> sendErrorMessage(ResponseResult responseResult, StompHeaderAccessor accessor) {
        byte[] payload;
        try {
            payload = new ObjectMapper().writeValueAsBytes(responseResult);
            return MessageBuilder.createMessage(payload, accessor.getMessageHeaders());
        } catch (JsonProcessingException e) {
            log.debug("Json 파싱 에러");
        }
        return null;
    }

    private Message<byte[]> sendServerErrorMessage(StompHeaderAccessor accessor, String message) {
        ResponseResult responseResult = ResponseResult.failed(HttpStatus.INTERNAL_SERVER_ERROR, message, null);
        return sendErrorMessage(responseResult, accessor);
    }

    private Message<byte[]> sendCustomExceptionMessage(CustomException ex, StompHeaderAccessor accessor) {
        ResponseResult responseResult = ResponseResult.failed(ex.getStatus(), ex.getMessage(), null);
        return sendErrorMessage(responseResult, accessor);
    }

    private Throwable parseException(final Throwable exception) {
        if (exception instanceof MessageDeliveryException) {
            return exception.getCause();
        }
        return exception;
    }
}
