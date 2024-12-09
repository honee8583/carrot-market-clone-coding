package com.carrot.carrotmarketclonecoding.chat.helper;

import com.carrot.carrotmarketclonecoding.chat.dto.ChatMessageResponseDto;
import com.carrot.carrotmarketclonecoding.chat.dto.ChatRoomResponseDto;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class ChatDtoFactory {

    public List<ChatRoomResponseDto> createChatRoomResponseDtos() {
        return Arrays.asList(
                ChatRoomResponseDto.builder()
                        .id(1L)
                        .boardId(1L)
                        .roomNum("room1")
                        .createDate(LocalDateTime.now())
                        .build(),
                ChatRoomResponseDto.builder()
                        .id(2L)
                        .boardId(2L)
                        .roomNum("room2")
                        .createDate(LocalDateTime.now())
                        .build(),
                ChatRoomResponseDto.builder()
                        .id(3L)
                        .boardId(3L)
                        .roomNum("room3")
                        .createDate(LocalDateTime.now())
                        .build()
        );
    }

    public List<ChatMessageResponseDto> createChatMessageResponseDtos() {
        return Arrays.asList(
                ChatMessageResponseDto.builder()
                        .id("6736b9de2fe0d671812deafc")
                        .senderId(3948203L)
                        .receiverId(2394840L)
                        .roomNum(UUID.randomUUID().toString())
                        .message("test message content")
                        .createDate(LocalDateTime.now())
                        .build(),
                ChatMessageResponseDto.builder()
                        .id("6736b87082a20d63569446cb")
                        .senderId(2394840L)
                        .receiverId(3948203L)
                        .roomNum(UUID.randomUUID().toString())
                        .message("test message content")
                        .createDate(LocalDateTime.now())
                        .build()
        );
    }
}
