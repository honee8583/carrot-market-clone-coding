package com.carrot.carrotmarketclonecoding.chat.dto;

import com.carrot.carrotmarketclonecoding.chat.domain.ChatRoom;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponseDto {
    private Long id;
    private Long boardId;
    private String roomNum;
    private LocalDateTime createDate;

    public static ChatRoomResponseDto createChatRoomResponseDto(ChatRoom chatRoom) {
        return ChatRoomResponseDto.builder()
                .id(chatRoom.getId())
                .boardId(chatRoom.getBoard().getId())
                .roomNum(chatRoom.getRoomNum())
                .createDate(chatRoom.getCreateDate())
                .build();
    }
}
