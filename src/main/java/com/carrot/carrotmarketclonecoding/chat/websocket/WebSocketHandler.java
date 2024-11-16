package com.carrot.carrotmarketclonecoding.chat.websocket;

import com.carrot.carrotmarketclonecoding.chat.dto.ChatMessageRequestDto;
import com.carrot.carrotmarketclonecoding.chat.service.ChatMessageService;
import com.carrot.carrotmarketclonecoding.chat.service.ChatRoomService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    private static final Map<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();

    private static final String ROOM_NUM_KEY = "roomNum";
    private static final String SENDER_KEY = "senderId";
    private static final String RECEIVER_KEY = "receiverId";

    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        URI uri = session.getUri();
        if (uri != null) {
            Map<String, String> parameters = parseQueryParams(uri.getQuery());
            checkChatRoom(parameters);
            rooms.computeIfAbsent(parameters.get(ROOM_NUM_KEY), k -> new HashSet<>()).add(session);
        } else {
            log.warn("채팅방 번호가 존재하지 않는 세션입니다: 세션 ID: {}", session.getId());
            session.close();
        }
    }

    private void checkChatRoom(Map<String, String> parameters) {
        String roomNum = parameters.get(ROOM_NUM_KEY);
        Long senderId = Long.parseLong(parameters.get(SENDER_KEY));
        Long receiverId = Long.parseLong(parameters.get(RECEIVER_KEY));
        chatRoomService.validateChatRoom(roomNum, senderId, receiverId);

        log.debug("WebSocket 연결 -> roomNum: {}, senderId: {}, receiverId: {}", roomNum, senderId, receiverId);
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> paramMap = new HashMap<>();
        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    paramMap.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return paramMap;
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("웹 소켓 에러 -> session id={}, error={}", session.getId(), exception.getMessage());
        removeSessionFromRoom(session);
        session.close();
        logRoomSize();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.debug("웹 소켓 종료 -> sessionId: {}", session.getId());
        removeSessionFromRoom(session);
        session.close();
        logRoomSize();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.debug("웹 소켓 메시지 전송 -> sessionId: {}", session.getId());
        ChatMessageRequestDto chatMessageRequestDto = readAndSaveMessage(message.getPayload());
        sendMessage(session, message, chatMessageRequestDto.getRoomNum());
    }
    
    private ChatMessageRequestDto readAndSaveMessage(String message) {
        ChatMessageRequestDto chatMessageRequestDto = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            chatMessageRequestDto = objectMapper.readValue(message, ChatMessageRequestDto.class);
        } catch (Exception e) {
            log.error("메시지 바인딩 에러!, message: {}", message);
        }
        chatMessageService.save(chatMessageRequestDto);

        return chatMessageRequestDto;
    }
    
    private void sendMessage(WebSocketSession session, TextMessage message, String roomNum) {
        Set<WebSocketSession> sessions = rooms.get(roomNum);
        sessions.forEach(s -> {
            if (!s.getId().equals(session.getId())) {
                try {
                    s.sendMessage(message);
                } catch (IOException e) {
                    log.error("메시지 전송 에러! -> roomNum: {}, session: {}", roomNum, session.getId());
                    throw new RuntimeException("Send Message Failed!");
                }
            }
        });
    }

    private void removeSessionFromRoom(WebSocketSession session) {
        rooms.forEach((roomNum, sessions) -> {
            if (sessions.contains(session)) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    rooms.remove(roomNum);
                }
            }
        });
    }

    private void logRoomSize() {
        log.debug("남은 채팅방 개수: {}", rooms.size());
    }
}