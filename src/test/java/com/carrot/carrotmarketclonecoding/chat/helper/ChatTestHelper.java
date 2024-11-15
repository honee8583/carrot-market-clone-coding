package com.carrot.carrotmarketclonecoding.chat.helper;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.carrot.carrotmarketclonecoding.chat.dto.ChatMessageResponseDto;
import com.carrot.carrotmarketclonecoding.chat.dto.ChatRoomRequestDto.ChatRoomCreateRequestDto;
import com.carrot.carrotmarketclonecoding.util.ControllerTestUtil;
import com.carrot.carrotmarketclonecoding.util.ResultFields;
import java.util.List;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;

public class ChatTestHelper extends ControllerTestUtil {

    private final ChatRequestHelper requestHelper;
    private final ChatRestDocsHelper restDocsHelper;

    public ChatTestHelper(MockMvc mvc, RestDocumentationResultHandler restDocs) {
        this.requestHelper = new ChatRequestHelper(mvc);
        this.restDocsHelper = new ChatRestDocsHelper(restDocs);
    }

    public void assertCreateChatRoom(ChatRoomCreateRequestDto request, ResultFields resultFields, String roomNum) throws Exception {
        assertResponseResult(requestHelper.requestCreateChatRoom(request), resultFields)
                .andExpect(jsonPath("$.data", equalTo(roomNum)))
                .andDo(restDocsHelper.createCreateChatRoomDocument());
    }

    public void assertCreateChatRoomFail(ChatRoomCreateRequestDto request, ResultFields resultFields) throws Exception {
        assertResponseResult(requestHelper.requestCreateChatRoom(request), resultFields)
                .andExpect(jsonPath("$.data", equalTo(null)))
                .andDo(restDocsHelper.createResponseResultDocument());
    }

    public void assertGetChatRooms(ResultFields resultFields) throws Exception {
        assertResponseResult(requestHelper.requestGetChatRooms(), resultFields)
                .andExpect(jsonPath("$.data.size()", equalTo(3)))
                .andDo(restDocsHelper.createGetChatRoomsDocument());
    }

    public void assertGetChatRoomsFail(ResultFields resultFields) throws Exception {
        assertResponseResult(requestHelper.requestGetChatRooms(), resultFields)
                .andExpect(jsonPath("$.data", equalTo(null)))
                .andDo(restDocsHelper.createResponseResultDocument());
    }

    public void assertGetChatMessagesSuccess(ResultFields resultFields, List<ChatMessageResponseDto> chatMessages) throws Exception {
        assertResponseResult(requestHelper.requestGetChatMessages(), resultFields)
                .andExpect(jsonPath("$.data.size()", equalTo(chatMessages.size())))
                .andDo(restDocsHelper.createGetChatMessagesDocument());
    }

    public void assertGetChatMessagesFail(ResultFields resultFields) throws Exception {
        assertResponseResult(requestHelper.requestGetChatMessages(), resultFields)
                .andExpect(jsonPath("$.data", equalTo(null)))
                .andDo(restDocsHelper.createGetChatMessagesFailDocument());
    }

    public void assertDeleteChatRoom(ResultFields resultFields) throws Exception {
        assertResponseResult(requestHelper.requestDeleteChatRoom(), resultFields)
                .andExpect(jsonPath("$.data", equalTo(null)))
                .andDo(restDocsHelper.createDeleteChatRoomDocument());
    }
}
