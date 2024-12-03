package com.carrot.carrotmarketclonecoding.chat.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private Long senderId;
    private String message;
}
