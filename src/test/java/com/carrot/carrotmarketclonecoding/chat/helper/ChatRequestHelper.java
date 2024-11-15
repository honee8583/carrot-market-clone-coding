package com.carrot.carrotmarketclonecoding.chat.helper;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;

import com.carrot.carrotmarketclonecoding.chat.dto.ChatRoomRequestDto.ChatRoomCreateRequestDto;
import com.carrot.carrotmarketclonecoding.util.ControllerRequestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@RequiredArgsConstructor
public class ChatRequestHelper extends ControllerRequestUtil {

    private final MockMvc mvc;

    private static final String CHAT_URL = "/chat/room";

    public ResultActions requestCreateChatRoom(ChatRoomCreateRequestDto request) throws Exception {
        return mvc.perform(
                requestWithCsrfAndSetContentType(post(CHAT_URL), MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
        );
    }

    public ResultActions requestGetChatRooms() throws Exception {
        return mvc.perform(get(CHAT_URL));
    }

    public ResultActions requestGetChatMessages() throws Exception {
        return mvc.perform(get(CHAT_URL + "/{id}", 1L));
    }

    public ResultActions requestDeleteChatRoom() throws Exception {
        return mvc.perform(requestWithCsrf(delete(CHAT_URL + "/{id}", 1L)));
    }
}
