package com.carrot.carrotmarketclonecoding.chat.service;

import com.carrot.carrotmarketclonecoding.chat.dto.ChatMessageRequestDto;
import com.carrot.carrotmarketclonecoding.chat.dto.ChatMessageResponseDto;
import java.util.List;

public interface ChatMessageService {

    void save(ChatMessageRequestDto chatMessageRequestDto);

    List<ChatMessageResponseDto> getChatMessages(Long authId, Long chatRoomId);

    void deleteAll(String roomNum);
}
