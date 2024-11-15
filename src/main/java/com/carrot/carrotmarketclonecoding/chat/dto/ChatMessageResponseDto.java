package com.carrot.carrotmarketclonecoding.chat.dto;

import com.carrot.carrotmarketclonecoding.chat.domain.ChatMessage;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponseDto {
    private String id;
    private String roomNum;
    private Long senderId;
    private Long receiverId;
    private String message;
    private LocalDateTime createDate;

    public ChatMessageResponseDto(ChatMessage chatMessage) {
        this.id = chatMessage.getId();
        this.roomNum = chatMessage.getRoomNum();
        this.senderId = chatMessage.getSenderId();
        this.receiverId = chatMessage.getReceiverId();
        this.message = chatMessage.getMessage();
        this.createDate = chatMessage.getCreateDate();
    }
}
