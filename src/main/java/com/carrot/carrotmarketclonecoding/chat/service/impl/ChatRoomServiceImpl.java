package com.carrot.carrotmarketclonecoding.chat.service.impl;

import com.carrot.carrotmarketclonecoding.chat.domain.ChatRoom;
import com.carrot.carrotmarketclonecoding.chat.dto.ChatRoomRequestDto.ChatRoomCreateRequestDto;
import com.carrot.carrotmarketclonecoding.chat.dto.ChatRoomResponseDto;
import com.carrot.carrotmarketclonecoding.chat.repository.ChatRoomRepository;
import com.carrot.carrotmarketclonecoding.chat.service.ChatMessageService;
import com.carrot.carrotmarketclonecoding.chat.service.ChatRoomService;
import com.carrot.carrotmarketclonecoding.common.exception.ChatRoomNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.NotMemberOfChatRoomException;
import com.carrot.carrotmarketclonecoding.common.exception.UserNotOwnerOfChatRoomException;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageService chatMessageService;

    @Override
    public String create(Long authId, ChatRoomCreateRequestDto createRequestDto) {
        Member sender = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
        Member receiver = memberRepository.findByAuthId(createRequestDto.getReceiverId()).orElseThrow(MemberNotFoundException::new);

        String roomNum = UUID.randomUUID().toString();
        ChatRoom chatRoom = ChatRoom.builder()
                .roomNum(roomNum)
                .sender(sender)
                .receiver(receiver)
                .build();
        chatRoomRepository.save(chatRoom);
        return roomNum;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomResponseDto> getChatRooms(Long authId) {
        Member member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
        List<ChatRoom> chatRoomsSended = chatRoomRepository.findAllBySender(member);
        List<ChatRoom> chatRoomsReceived = chatRoomRepository.findAllByReceiver(member);
        return Stream.concat(
                        chatRoomsSended.stream().map(ChatRoomResponseDto::createChatRoomResponseDto),
                        chatRoomsReceived.stream().map(ChatRoomResponseDto::createChatRoomResponseDto)
                )
                .sorted((d1, d2) -> d2.getCreateDate().compareTo(d1.getCreateDate()))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long authId, Long chatRoomId) {
        Member member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(ChatRoomNotFoundException::new);
        validateSenderOfChatRoom(chatRoom, member);
        chatMessageService.deleteAll(chatRoom.getRoomNum());
        chatRoomRepository.delete(chatRoom);
    }

    private void validateSenderOfChatRoom(ChatRoom chatRoom, Member sender) {
        if (chatRoom.getSender() != sender) {
            throw new UserNotOwnerOfChatRoomException();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void validateChatRoom(String roomNum, Long senderId, Long receiverId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomNum(roomNum).orElseThrow(ChatRoomNotFoundException::new);
        validateSenderAndReceiverOfChatRoom(chatRoom, senderId, receiverId);
    }

    public void validateSenderAndReceiverOfChatRoom(ChatRoom chatRoom, Long senderId, Long receiverId) {
        Long savedSenderId = chatRoom.getSender().getAuthId();
        Long savedReceiverId = chatRoom.getReceiver().getAuthId();
        if (!savedSenderId.equals(senderId) && !savedSenderId.equals(receiverId)) {
            throw new NotMemberOfChatRoomException();
        }
        if (!savedReceiverId.equals(receiverId) && !savedReceiverId.equals(senderId)) {
            throw new NotMemberOfChatRoomException();
        }
    }
}
