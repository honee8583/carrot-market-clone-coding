package com.carrot.carrotmarketclonecoding.chat.controller;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.*;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrot.carrotmarketclonecoding.ControllerTest;
import com.carrot.carrotmarketclonecoding.chat.dto.ChatMessageResponseDto;
import com.carrot.carrotmarketclonecoding.chat.dto.ChatRoomRequestDto.ChatRoomCreateRequestDto;
import com.carrot.carrotmarketclonecoding.chat.dto.ChatRoomResponseDto;
import com.carrot.carrotmarketclonecoding.chat.helper.ChatDtoFactory;
import com.carrot.carrotmarketclonecoding.chat.helper.ChatTestHelper;
import com.carrot.carrotmarketclonecoding.common.exception.ChatRoomNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.NotMemberOfChatRoomException;
import com.carrot.carrotmarketclonecoding.common.exception.UserNotOwnerOfChatRoomException;
import com.carrot.carrotmarketclonecoding.util.ResultFields;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ChatControllerTest extends ControllerTest {

    private ChatTestHelper testHelper;

    @Autowired
    private ChatDtoFactory dtoFactory;

    @BeforeEach
    void setUp() {
        this.testHelper = new ChatTestHelper(mvc, restDocs);
    }

    private ChatRoomCreateRequestDto request = ChatRoomCreateRequestDto.builder()
            .boardId(1L)
            .receiverId(2L)
            .build();

    @Nested
    @DisplayName("채팅방 생성 컨트롤러 테스트")
    class CreateChatRoomTests {

        @Test
        @DisplayName("채팅방 생성 API 요청 성공")
        void createChatRoomTest() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isCreated())
                    .status(201)
                    .result(true)
                    .message(CREATE_CHAT_ROOM_SUCCESS.getMessage())
                    .build();

            // when
            when(chatRoomService.create(anyLong(), any(ChatRoomCreateRequestDto.class))).thenReturn("test room");

            // then
            testHelper.assertCreateChatRoom(request, resultFields, "test room");
        }

        @Test
        @DisplayName("채팅방 생성 API 요청시 전송자/수신자 데이터가 없을 경우 실패")
        void createChatRoom_SenderOrReceiverNotFoundTest() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(MemberNotFoundException.class).when(chatRoomService).create(anyLong(), any(ChatRoomCreateRequestDto.class));

            // then
            testHelper.assertCreateChatRoomFail(request, resultFields);
        }
    }

    @Nested
    @DisplayName("채팅방 목록 조회 컨트롤러 테스트")
    class GetChatRoomsTests {

        @Test
        @DisplayName("채팅방 목록 조회 API 요청 성공")
        void getChatRoomsTest() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(GET_CHAT_ROOMS_SUCCESS.getMessage())
                    .build();

            List<ChatRoomResponseDto> response = dtoFactory.createChatRoomResponseDtos();

            // when
            when(chatRoomService.getChatRooms(anyLong())).thenReturn(response);

            // then
            testHelper.assertGetChatRooms(resultFields);
        }

        @Test
        @DisplayName("채팅방 목록 조회 시 사용자가 존재하지 않을 경우 실패")
        void getChatRooms_MemberNotFoundTest() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(MemberNotFoundException.class).when(chatRoomService).getChatRooms(anyLong());

            // then
            testHelper.assertGetChatRoomsFail(resultFields);
        }
    }

    @Nested
    @DisplayName("채팅내역 조회 컨트롤러 테스트")
    class GetChatMessagesTests {

        @Test
        @DisplayName("채팅방의 채팅내역 조회 API 요청 성공")
        void getChatMessagesTest() throws Exception {
            // given
            List<ChatMessageResponseDto> chatMessages = dtoFactory.createChatMessageResponseDtos();

            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(GET_CHAT_MESSAGES_SUCCESS.getMessage())
                    .build();

            // when
            when(chatMessageService.getChatMessages(anyLong(), anyLong())).thenReturn(chatMessages);

            // then
            testHelper.assertGetChatMessagesSuccess(resultFields, chatMessages);
        }

        @Test
        @DisplayName("채팅내역을 조회할 채팅방이 존재하지 않음")
        void getChatMessages_ChatRoomNotFoundTest() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isBadRequest())
                    .status(400)
                    .result(false)
                    .message(CHAT_ROOM_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(ChatRoomNotFoundException.class).when(chatMessageService).getChatMessages(anyLong(), anyLong());

            // then
            testHelper.assertGetChatMessagesFail(resultFields);
        }

        @Test
        @DisplayName("채팅내역 조회시 사용자의 채팅방이 아님")
        void getChatMessages_NotMemberOfChatRoomTest() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isForbidden())
                    .status(403)
                    .result(false)
                    .message(NOT_MEMBER_OF_CHAT_ROOM.getMessage())
                    .build();

            // when
            doThrow(NotMemberOfChatRoomException.class).when(chatMessageService).getChatMessages(anyLong(), anyLong());

            // then
            testHelper.assertGetChatMessagesFail(resultFields);
        }
    }

    @Nested
    @DisplayName("채팅방 삭제 컨트롤러 테스트")
    class DeleteChatRoomTests {

        @Test
        @DisplayName("채팅방 삭제 API 요청 성공")
        void deleteChatRoomTest() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(DELETE_CHAT_ROOM_SUCCESS.getMessage())
                    .build();

            // when
            doNothing().when(chatRoomService).delete(anyLong(), anyLong());

            // then
            testHelper.assertDeleteChatRoom(resultFields);
        }

        @Test
        @DisplayName("삭제 요청한 채팅방이 사용자의 채팅방이 아닌 경우 실패")
        void deleteChatRoom_UserIsNotOwnerTest() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isForbidden())
                    .status(403)
                    .result(false)
                    .message(FORBIDDEN.getMessage())
                    .build();

            // when
            doThrow(UserNotOwnerOfChatRoomException.class).when(chatRoomService).delete(anyLong(), anyLong());

            // then
            testHelper.assertDeleteChatRoom(resultFields);
        }

        @Test
        @DisplayName("채팅방 삭제 요청시 사용자가 존재하지 않을 경우 실패")
        void deleteChatRoom_MemberNotFoundTest() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(MemberNotFoundException.class).when(chatRoomService).delete(anyLong(), anyLong());

            // then
            testHelper.assertDeleteChatRoom(resultFields);
        }

        @Test
        @DisplayName("삭제 요청한 채팅방이 존재하지 않을 경우 실패")
        void deleteChatRoom_ChatRoomNotFoundTest() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isBadRequest())
                    .status(400)
                    .result(false)
                    .message(CHAT_ROOM_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(ChatRoomNotFoundException.class).when(chatRoomService).delete(anyLong(), anyLong());

            // then
            testHelper.assertDeleteChatRoom(resultFields);
        }
    }
}