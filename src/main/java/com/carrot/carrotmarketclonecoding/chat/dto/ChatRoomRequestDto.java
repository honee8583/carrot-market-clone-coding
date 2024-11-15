package com.carrot.carrotmarketclonecoding.chat.dto;

import lombok.*;

public class ChatRoomRequestDto {

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRoomCreateRequestDto {
        private Long senderId;  // TODO remove field senderId
        private Long receiverId;
    }
}
