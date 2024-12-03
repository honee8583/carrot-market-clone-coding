package com.carrot.carrotmarketclonecoding.chat.helper;

import com.carrot.carrotmarketclonecoding.common.response.ResponseResult;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

public class StompSessionHandler extends StompSessionHandlerAdapter {

    private BlockingQueue<ResponseResult> messages;

    public StompSessionHandler(BlockingQueue messages) {
        this.messages = messages;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return super.getPayloadType(headers);
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        System.out.println("오류메시지를 추가합니다.");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ResponseResult responseResult = objectMapper.readValue((String) payload, ResponseResult.class);
            messages.offer(responseResult);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        super.handleFrame(headers, payload);
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        System.out.println("연결됨");
        super.afterConnected(session, connectedHeaders);
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers,
                                byte[] payload, Throwable exception) {
        try {
            System.out.println("Handle Exception -> " + new ObjectMapper().readValue(payload, String.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        super.handleException(session, command, headers, payload, exception);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        super.handleTransportError(session, exception);
    }
}
