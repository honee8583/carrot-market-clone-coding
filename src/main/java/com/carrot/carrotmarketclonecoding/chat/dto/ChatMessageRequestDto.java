package com.carrot.carrotmarketclonecoding.chat.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequestDto {
    private Long senderId;
    private Long receiverId;
    private String message;
    private String roomNum;
}
