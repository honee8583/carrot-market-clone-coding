package com.carrot.carrotmarketclonecoding.chat.helper;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

public class StompFrameHandlerImpl<T> implements StompFrameHandler {

    private final T payload;
    private final BlockingQueue<T> messages;

    public StompFrameHandlerImpl(final T payload, final BlockingQueue<T> messages) {
        this.payload = payload;
        this.messages = messages;
    }

    @Override
    public Type getPayloadType(final StompHeaders headers) {
        return payload.getClass();
    }

    @Override
    public void handleFrame(final StompHeaders headers, final Object payload) {
        messages.offer((T) payload);
    }
}
