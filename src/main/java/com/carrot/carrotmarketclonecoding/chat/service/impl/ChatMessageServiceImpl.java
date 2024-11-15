package com.carrot.carrotmarketclonecoding.chat.service.impl;

import com.carrot.carrotmarketclonecoding.chat.domain.ChatMessage;
import com.carrot.carrotmarketclonecoding.chat.domain.ChatRoom;
import com.carrot.carrotmarketclonecoding.chat.dto.ChatMessageRequestDto;
import com.carrot.carrotmarketclonecoding.chat.dto.ChatMessageResponseDto;
import com.carrot.carrotmarketclonecoding.chat.repository.ChatMessageRepository;
import com.carrot.carrotmarketclonecoding.chat.repository.ChatRoomRepository;
import com.carrot.carrotmarketclonecoding.chat.service.ChatMessageService;
import com.carrot.carrotmarketclonecoding.common.exception.ChatRoomNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.NotMemberOfChatRoomException;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private final MemberRepository memberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    public void save(ChatMessageRequestDto chatMessageRequestDto) {
        Member sender = memberRepository.findByAuthId(chatMessageRequestDto.getSenderId()).orElseThrow(MemberNotFoundException::new);
        Member receiver = memberRepository.findByAuthId(chatMessageRequestDto.getReceiverId()).orElseThrow(MemberNotFoundException::new);
        ChatMessage message = ChatMessage.builder()
                .senderId(sender.getAuthId())
                .receiverId(receiver.getAuthId())
                .message(chatMessageRequestDto.getMessage())
                .roomNum(chatMessageRequestDto.getRoomNum())
                .createDate(LocalDateTime.now())
                .build();
        chatMessageRepository.save(message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponseDto> getChatMessages(Long authId, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(ChatRoomNotFoundException::new);
        validateMemberOfChatRoom(authId, chatRoom);
        List<ChatMessage> chatMessages = chatMessageRepository.findAllByRoomNumOrderByCreateDateDesc(chatRoom.getRoomNum());
        return chatMessages.stream().map(ChatMessageResponseDto::new).collect(Collectors.toList());
    }

    private void validateMemberOfChatRoom(Long authId, ChatRoom chatRoom) {
        if (!authId.equals(chatRoom.getSender().getAuthId()) && !authId.equals(chatRoom.getReceiver().getAuthId())) {
            throw new NotMemberOfChatRoomException();
        }
    }

    @Override
    public void deleteAll(String roomNum) {
        chatMessageRepository.deleteAllByRoomNum(roomNum);
    }
}
