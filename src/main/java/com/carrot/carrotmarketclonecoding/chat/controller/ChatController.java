package com.carrot.carrotmarketclonecoding.chat.controller;

import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.CREATE_CHAT_ROOM_SUCCESS;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.DELETE_CHAT_ROOM_SUCCESS;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.GET_CHAT_MESSAGES_SUCCESS;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.GET_CHAT_ROOMS_SUCCESS;

import com.carrot.carrotmarketclonecoding.auth.dto.LoginUser;
import com.carrot.carrotmarketclonecoding.chat.dto.ChatMessageResponseDto;
import com.carrot.carrotmarketclonecoding.chat.dto.ChatRoomRequestDto.ChatRoomCreateRequestDto;
import com.carrot.carrotmarketclonecoding.chat.dto.ChatRoomResponseDto;
import com.carrot.carrotmarketclonecoding.chat.service.ChatMessageService;
import com.carrot.carrotmarketclonecoding.chat.service.ChatRoomService;
import com.carrot.carrotmarketclonecoding.common.response.ResponseResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/chat/room")
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @PostMapping
    public ResponseEntity<?> createChatRoom(@AuthenticationPrincipal LoginUser loginUser,
                                            @RequestBody ChatRoomCreateRequestDto chatRoomCreateRequestDto) {
        String roomNum = chatRoomService.create(Long.parseLong(loginUser.getUsername()), chatRoomCreateRequestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseResult.success(HttpStatus.CREATED, CREATE_CHAT_ROOM_SUCCESS.getMessage(), roomNum));
    }

    @GetMapping
    public ResponseEntity<?> getChatRooms(@AuthenticationPrincipal LoginUser loginUser) {
        List<ChatRoomResponseDto> chatRooms = chatRoomService.getChatRooms(Long.parseLong(loginUser.getUsername()));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, GET_CHAT_ROOMS_SUCCESS.getMessage(), chatRooms));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getChatMessages(@AuthenticationPrincipal LoginUser loginUser,
                                             @PathVariable("id") Long chatRoomId) {
        List<ChatMessageResponseDto> chatMessages = chatMessageService.getChatMessages(
                Long.parseLong(loginUser.getUsername()), chatRoomId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, GET_CHAT_MESSAGES_SUCCESS.getMessage(), chatMessages));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChatRoom(@AuthenticationPrincipal LoginUser loginUser,
                                            @PathVariable("id") Long chatRoomId) {
        chatRoomService.delete(Long.parseLong(loginUser.getUsername()), chatRoomId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, DELETE_CHAT_ROOM_SUCCESS.getMessage(), null));
    }
}
