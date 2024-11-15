package com.carrot.carrotmarketclonecoding.chat.service.impl;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.CHAT_ROOM_NOT_FOUND;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.MEMBER_NOT_FOUND;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.NOT_MEMBER_OF_CHAT_ROOM;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.carrot.carrotmarketclonecoding.chat.domain.ChatMessage;
import com.carrot.carrotmarketclonecoding.chat.domain.ChatRoom;
import com.carrot.carrotmarketclonecoding.chat.dto.ChatMessageRequestDto;
import com.carrot.carrotmarketclonecoding.chat.dto.ChatMessageResponseDto;
import com.carrot.carrotmarketclonecoding.chat.repository.ChatMessageRepository;
import com.carrot.carrotmarketclonecoding.chat.repository.ChatRoomRepository;
import com.carrot.carrotmarketclonecoding.common.exception.ChatRoomNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.NotMemberOfChatRoomException;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @InjectMocks
    private ChatMessageServiceImpl chatMessageService;

    @Nested
    @DisplayName("채팅메시지 저장 서비스 테스트")
    class SaveChatMessageTests {

        @Test
        @DisplayName("채팅메시지 저장 성공")
        void saveChatMessageTest() {
            // given
            Member mockSender = Member.builder().authId(1L).build();
            when(memberRepository.findByAuthId(1L)).thenReturn(Optional.of(mockSender));

            Member mockReceiver = Member.builder().authId(2L).build();
            when(memberRepository.findByAuthId(2L)).thenReturn(Optional.of(mockReceiver));

            // when
            ChatMessageRequestDto request = ChatMessageRequestDto.builder()
                    .senderId(1L)
                    .receiverId(2L)
                    .message("test message")
                    .roomNum("test room")
                    .build();
            chatMessageService.save(request);

            // then
            ArgumentCaptor<ChatMessage> chatMessageArgumentCaptor = ArgumentCaptor.forClass(ChatMessage.class);
            verify(chatMessageRepository, times(1)).save(chatMessageArgumentCaptor.capture());
            ChatMessage chatMessage = chatMessageArgumentCaptor.getValue();
            assertThat(chatMessage.getSenderId()).isEqualTo(request.getSenderId());
            assertThat(chatMessage.getReceiverId()).isEqualTo(request.getReceiverId());
            assertThat(chatMessage.getMessage()).isEqualTo(request.getMessage());
            assertThat(chatMessage.getRoomNum()).isEqualTo(request.getRoomNum());
        }

        @Test
        @DisplayName("채팅메시지 저장시 사용자가 존재하지 않을 경우 예외 발생")
        void saveChatMessageMemberNotFoundTest() {
            // given
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> chatMessageService.save(mock(ChatMessageRequestDto.class)))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("채팅방의 채팅메시지 목록 조회 서비스 테스트")
    class GetChatMessagesTests {

        @Test
        @DisplayName("채팅방의 채팅메시지 목록 조회 성공")
        void getChatMessagesTest() {
            // given
            String roomNum = "test room";
            ChatRoom mockChatRoom = ChatRoom.builder()
                    .id(1L)
                    .sender(Member.builder().authId(1111L).build())
                    .receiver(Member.builder().authId(2222L).build())
                    .roomNum(roomNum)
                    .build();
            when(chatRoomRepository.findById(anyLong())).thenReturn(Optional.of(mockChatRoom));

            List<ChatMessage> chatMessages = Arrays.asList(
                    ChatMessage.builder().build(),
                    ChatMessage.builder().build(),
                    ChatMessage.builder().build()
            );
            when(chatMessageRepository.findAllByRoomNumOrderByCreateDateDesc(anyString())).thenReturn(chatMessages);

            // when
            List<ChatMessageResponseDto> chatMessagesResponse = chatMessageService.getChatMessages(1111L, 1L);

            // then
            assertThat(chatMessagesResponse.size()).isEqualTo(3);
        }

        @Test
        @DisplayName("채팅메시지 목록 조회시 채팅방이 존재하지 않을 경우 예외 발생")
        void getChatMessages_ChatRoomNotFound() {
            // given
            when(chatRoomRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> chatMessageService.getChatMessages(1111L, 1L))
                    .isInstanceOf(ChatRoomNotFoundException.class)
                    .hasMessage(CHAT_ROOM_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("채팅메시지 목록 조회시 사용자의 채팅방이 아닐 경우 예외 발생")
        void getChatMessages_NotMemberOfChatRoom() {
            // given
            ChatRoom mockChatRoom = ChatRoom.builder()
                    .id(1L)
                    .sender(Member.builder().authId(1111L).build())
                    .receiver(Member.builder().authId(2222L).build())
                    .build();
            when(chatRoomRepository.findById(anyLong())).thenReturn(Optional.of(mockChatRoom));

            // when
            // then
            assertThatThrownBy(() -> chatMessageService.getChatMessages(3333L, 1L))
                    .isInstanceOf(NotMemberOfChatRoomException.class)
                    .hasMessage(NOT_MEMBER_OF_CHAT_ROOM.getMessage());
        }
    }

    @Nested
    @DisplayName("채팅방의 모든 메세지 삭제 서비스 테스트")
    class DeleteAllChatMessagesTests {

        @Test
        @DisplayName("채팅방의 모든 메세지 삭제 성공")
        void deleteAllTest() {
            // given
            String roomNum = "test room";

            // when
            chatMessageService.deleteAll(roomNum);

            // then
            verify(chatMessageRepository, times(1)).deleteAllByRoomNum(roomNum);
        }
    }
}