package com.carrot.carrotmarketclonecoding.chat.controller;

import com.carrot.carrotmarketclonecoding.chat.dto.ChatMessageRequestDto;
import com.carrot.carrotmarketclonecoding.chat.dto.ChatMessageResponse;
import com.carrot.carrotmarketclonecoding.chat.service.ChatMessageService;
import com.carrot.carrotmarketclonecoding.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StompController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat.{chatRoomId}")
    @SendTo("/topic/chat.{chatRoomId}")
    public ChatMessageResponse sendMessage(ChatMessageRequestDto request,
                                           @DestinationVariable String chatRoomId) {
        log.debug("request -> {}, chatRoomId -> {}", request, chatRoomId);

        request.setRoomNum(chatRoomId);
        chatRoomService.validateChatRoom(request);
        chatMessageService.save(request);

        return new ChatMessageResponse(request.getSenderId(), request.getMessage());
    }
}
