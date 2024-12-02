package com.carrot.carrotmarketclonecoding.chat.service;

import com.carrot.carrotmarketclonecoding.chat.dto.ChatMessageRequestDto;
import com.carrot.carrotmarketclonecoding.chat.dto.ChatRoomRequestDto.ChatRoomCreateRequestDto;
import com.carrot.carrotmarketclonecoding.chat.dto.ChatRoomResponseDto;
import java.util.List;

public interface ChatRoomService {

    String create(Long authId, ChatRoomCreateRequestDto createRequestDto);

    List<ChatRoomResponseDto> getChatRooms(Long authId);

    void delete(Long authId, Long chatRoomId);

    void validateChatRoom(ChatMessageRequestDto messageRequestDto);
}
