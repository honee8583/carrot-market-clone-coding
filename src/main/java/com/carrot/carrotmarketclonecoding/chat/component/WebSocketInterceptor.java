package com.carrot.carrotmarketclonecoding.chat.component;

import com.carrot.carrotmarketclonecoding.auth.dto.JwtVO;
import com.carrot.carrotmarketclonecoding.auth.util.JwtUtil;
import com.carrot.carrotmarketclonecoding.common.exception.JwtTokenNotExistsException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.debug("ChannelInterceptor...");

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        StompCommand command = headerAccessor.getCommand();
        log.debug("STOMP Command -> {}", command);

        if (command != null && command != StompCommand.DISCONNECT && command != StompCommand.CONNECT) {
            List<String> headers = headerAccessor.getNativeHeader(JwtVO.HEADER);
            validateHeader(headers);
            validateToken(headers.get(0));
        }

        return message;
    }

    private void validateHeader(List<String> headers) {
        if (headers == null || headers.isEmpty()) {
            log.debug("JWT 토큰헤더가 존재하지 않습니다!");
            throw new JwtTokenNotExistsException();
        }
    }

    private void validateToken(String token) {
        if (token.equals("null") || token.isEmpty()) {
            log.debug("JWT 토큰이 존재하지 않습니다!");
            throw new JwtTokenNotExistsException();
        } else {
            jwtUtil.verify(token);
        }
    }
}