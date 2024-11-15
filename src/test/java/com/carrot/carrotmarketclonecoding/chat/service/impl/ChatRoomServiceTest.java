package com.carrot.carrotmarketclonecoding.chat.service.impl;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.CHAT_ROOM_NOT_FOUND;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.FORBIDDEN;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.MEMBER_NOT_FOUND;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.NOT_MEMBER_OF_CHAT_ROOM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.carrot.carrotmarketclonecoding.chat.domain.ChatRoom;
import com.carrot.carrotmarketclonecoding.chat.dto.ChatRoomRequestDto.ChatRoomCreateRequestDto;
import com.carrot.carrotmarketclonecoding.chat.dto.ChatRoomResponseDto;
import com.carrot.carrotmarketclonecoding.chat.repository.ChatRoomRepository;
import com.carrot.carrotmarketclonecoding.common.exception.ChatRoomNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.NotMemberOfChatRoomException;
import com.carrot.carrotmarketclonecoding.common.exception.UserNotOwnerOfChatRoomException;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import java.time.LocalDateTime;
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
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ChatMessageServiceImpl chatMessageService;

    @InjectMocks
    private ChatRoomServiceImpl chatRoomService;

    @Nested
    @DisplayName("채팅방 생성 테스트")
    class CreateChatRoomTests {

        @Test
        @DisplayName("채팅방 저장 후 채팅방 번호 반환 성공")
        void createChatRoomTest() {
            // given
            Member mockSender = Member.builder().id(1L).authId(1L).build();
            when(memberRepository.findByAuthId(1L)).thenReturn(Optional.of(mockSender));

            Member mockReceiver = Member.builder().id(2L).authId(2L).build();
            when(memberRepository.findByAuthId(2L)).thenReturn(Optional.of(mockReceiver));

            // when
            ChatRoomCreateRequestDto request = ChatRoomCreateRequestDto.builder()
                    .receiverId(2L)
                    .build();
            String roomNum = chatRoomService.create(1L, request);

            // then
            assertThat(roomNum).isNotNull();

            ArgumentCaptor<ChatRoom> chatRoomArgumentCaptor = ArgumentCaptor.forClass(ChatRoom.class);
            verify(chatRoomRepository, times(1)).save(chatRoomArgumentCaptor.capture());

            ChatRoom chatRoom = chatRoomArgumentCaptor.getValue();
            assertThat(chatRoom.getSender().getAuthId()).isEqualTo(1L);
            assertThat(chatRoom.getReceiver().getAuthId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("채팅방 생성시 송신자가 존재하지 않을 경우 MemberNotFoundException 발생")
        void createChatRoomSenderNotFoundTest() {
            // given
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            ChatRoomCreateRequestDto request = ChatRoomCreateRequestDto.builder()
                    .receiverId(2L)
                    .build();
            assertThatThrownBy(() -> chatRoomService.create(1L, request))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("채팅방 생성시 수신자가 존재하지 않을 경우 MemberNotFoundException 발생")
        void createChatRoomReceiverNotFoundTest() {
            // given
            Member mockSender = Member.builder().id(1L).build();
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockSender));
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            ChatRoomCreateRequestDto request = ChatRoomCreateRequestDto.builder()
                    .receiverId(2L)
                    .build();
            assertThatThrownBy(() -> chatRoomService.create(1L, request))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("사용자의 채팅방 목록 조회")
    class GetChatRoomsTests {

        @Test
        @DisplayName("사용자의 채팅방 목록 조회 성공")
        void getChatRoomsTest() {
            // given
            Member mockMember = Member.builder()
                    .id(1L)
                    .build();
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));

            List<ChatRoom> chatRooms1 = List.of(
                    ChatRoom.builder().id(1L).build(),
                    ChatRoom.builder().id(2L).build(),
                    ChatRoom.builder().id(3L).build()
            );
            ReflectionTestUtils.setField(chatRooms1.get(0), "createDate", LocalDateTime.of(2024, 11, 1, 12, 0));
            ReflectionTestUtils.setField(chatRooms1.get(1), "createDate", LocalDateTime.of(2024, 11, 2, 12, 0));
            ReflectionTestUtils.setField(chatRooms1.get(2), "createDate", LocalDateTime.of(2024, 11, 3, 12, 0));
            when(chatRoomRepository.findAllBySender(any(Member.class))).thenReturn(chatRooms1);

            List<ChatRoom> chatRooms2 = List.of(
                    ChatRoom.builder().id(4L).build(),
                    ChatRoom.builder().id(5L).build(),
                    ChatRoom.builder().id(6L).build()
            );
            ReflectionTestUtils.setField(chatRooms2.get(0), "createDate", LocalDateTime.of(2024, 11, 4, 12, 0));
            ReflectionTestUtils.setField(chatRooms2.get(1), "createDate", LocalDateTime.of(2024, 11, 5, 12, 0));
            ReflectionTestUtils.setField(chatRooms2.get(2), "createDate", LocalDateTime.of(2024, 11, 6, 12, 0));
            when(chatRoomRepository.findAllByReceiver(any(Member.class))).thenReturn(chatRooms2);

            // when
            List<ChatRoomResponseDto> chatRoomDtos = chatRoomService.getChatRooms(1111L);

            // then
            assertThat(chatRoomDtos.size()).isEqualTo(6);
        }

        @Test
        @DisplayName("사용자의 채팅방 목록 조회시 사용자가 존재하지 않을 경우 MemberNotFound 예외 발생")
        void getCharRoomsMemberNotFoundTest() {
            // given
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> chatRoomService.getChatRooms(1111L))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("사용자의 채팅방 삭제")
    class DeleteChatRoomTests {

        @Test
        @DisplayName("사용자의 채팅방 삭제 성공")
        void deleteChatRoomTest() {
            // given
            Member mockMember = Member.builder()
                    .id(1L)
                    .build();
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));

            ChatRoom mockChatRoom = ChatRoom.builder()
                    .id(1L)
                    .roomNum("test room")
                    .sender(mockMember)
                    .build();
            when(chatRoomRepository.findById(anyLong())).thenReturn(Optional.of(mockChatRoom));

            // when
            chatRoomService.delete(1111L, 1L);

            // then
            verify(chatRoomRepository, times(1)).delete(mockChatRoom);
            verify(chatMessageService, times(1)).deleteAll(mockChatRoom.getRoomNum());
        }

        @Test
        @DisplayName("삭제하려는 채팅방이 사용자의 채팅방이 아닐경우 예외 발생")
        void deleteChatRoomUserIsNotOwnerTest() {
            // given
            Member mockMember = Member.builder()
                    .id(1L)
                    .build();
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));

            ChatRoom mockChatRoom = ChatRoom.builder()
                    .id(1L)
                    .sender(Member.builder().id(2L).build())
                    .build();
            when(chatRoomRepository.findById(anyLong())).thenReturn(Optional.of(mockChatRoom));

            // when
            // then
            assertThatThrownBy(() -> chatRoomService.delete(1111L, 1L))
                    .isInstanceOf(UserNotOwnerOfChatRoomException.class)
                    .hasMessage(FORBIDDEN.getMessage());
        }

        @Test
        @DisplayName("삭제하려는 채팅방의 사용자가 존재하지 않을 경우 예외 발생")
        void deleteChatRoomMemberNotFoundTest() {
            // given
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> chatRoomService.delete(1L, 1L))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("삭제하려는 채팅방이 존재하지 않을 경우 예외 발생")
        void deleteChatRoomButChatRoomNotFoundTest() {
            // given
            Member mockMember = Member.builder().id(1L).build();
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));
            when(chatRoomRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> chatRoomService.delete(1L, 1L))
                    .isInstanceOf(ChatRoomNotFoundException.class)
                    .hasMessage(CHAT_ROOM_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("채팅방의 사용자 확인 테스트")
    class ValidateChatRoomTests {

        @Test
        @DisplayName("채팅방의 사용자, 수신자가 맞는지 확인 테스트")
        void validateChatRoomOfSenderAndReceiverTest() {
            // given
            Member sender = Member.builder().id(1L).authId(1111L).build();
            Member receiver = Member.builder().id(2L).authId(2222L).build();
            ChatRoom chatRoom = ChatRoom.builder()
                    .roomNum("test room")
                    .sender(sender)
                    .receiver(receiver)
                    .build();

            // when
            when(chatRoomRepository.findByRoomNum(anyString())).thenReturn(Optional.of(chatRoom));

            // then
            chatRoomService.validateChatRoom("test room", 2222L, 1111L);
        }

        @Test
        @DisplayName("채팅방의 송신자/수신자와 맞지 않을 경우 예외 발생")
        void validateChatRoomOfSenderAndReceiver_NotMemberOfChatRoomTest() {
            // given
            Member sender = Member.builder().id(1L).authId(1111L).build();
            Member receiver = Member.builder().id(2L).authId(2222L).build();
            ChatRoom chatRoom = ChatRoom.builder()
                    .roomNum("test room")
                    .sender(sender)
                    .receiver(receiver)
                    .build();
            when(chatRoomRepository.findByRoomNum(anyString())).thenReturn(Optional.of(chatRoom));

            // when
            // then
            assertThatThrownBy(() -> chatRoomService.validateChatRoom("test room", 2222L, 3333L))
                    .isInstanceOf(NotMemberOfChatRoomException.class)
                    .hasMessage(NOT_MEMBER_OF_CHAT_ROOM.getMessage());
        }

        @Test
        @DisplayName("채팅방이 존재하지 않을 경우 예외 발생")
        void validateChatRoomOfSenderAndReceiver_ChatRoomNotFound() {
            // given
            when(chatRoomRepository.findByRoomNum(anyString())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> chatRoomService.validateChatRoom("test room", 1111L, 2222L))
                    .isInstanceOf(ChatRoomNotFoundException.class)
                    .hasMessage(CHAT_ROOM_NOT_FOUND.getMessage());
        }
    }
}