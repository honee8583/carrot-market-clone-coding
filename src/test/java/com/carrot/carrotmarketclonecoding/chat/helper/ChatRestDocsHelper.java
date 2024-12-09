package com.carrot.carrotmarketclonecoding.chat.helper;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

import com.carrot.carrotmarketclonecoding.util.RestDocsHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.ResultHandler;

@RequiredArgsConstructor
public class ChatRestDocsHelper extends RestDocsHelper {

    private final RestDocumentationResultHandler restDocs;

    public ResultHandler createCreateChatRoomDocument() {
        return restDocs.document(
                requestFields(
                        fieldWithPath("boardId").description("게시글 아이디"),
                        fieldWithPath("receiverId").description("수신자 아이디")
                ),
                responseFields(
                        addFieldsWithCommonFieldsDescriptors(getCreateChatRoomFieldDescriptors())
                )
        );
    }

    private FieldDescriptor[] getCreateChatRoomFieldDescriptors() {
        return new FieldDescriptor[]{
                fieldWithPath("data").description("채팅방번호")
        };
    }

    public ResultHandler createGetChatRoomsDocument() {
        return restDocs.document(
                responseFields(addFieldsWithCommonFieldsDescriptors(getChatRoomsFieldDescriptors()))
        );
    }

    private FieldDescriptor[] getChatRoomsFieldDescriptors() {
        return new FieldDescriptor[]{
                fieldWithPath("data[].id").description("채팅방 아이디"),
                fieldWithPath("data[].boardId").description("게시글 아이디"),
                fieldWithPath("data[].roomNum").description("채팅방 번호"),
                fieldWithPath("data[].createDate").description("채팅방 개설시간")
        };
    }

    public ResultHandler createGetChatMessagesDocument() {
        return restDocs.document(
                pathParameters(
                        parameterWithName("id").description("채팅방 아이디")
                ),
                responseFields(
                        addFieldsWithCommonFieldsDescriptors(
                                getChatMessageFieldDescriptors()
                        )
                )
        );
    }

    private FieldDescriptor[] getChatMessageFieldDescriptors() {
        return new FieldDescriptor[] {
                fieldWithPath("data[].id").description("채팅메시지 아이디"),
                fieldWithPath("data[].roomNum").description("채팅방 번호"),
                fieldWithPath("data[].senderId").description("채팅메시지 송신자 아이디"),
                fieldWithPath("data[].receiverId").description("채팅메시지 수신자 아이디"),
                fieldWithPath("data[].message").description("채팅메시지 내용"),
                fieldWithPath("data[].createDate").description("채팅메시지 생성일"),
        };
    }

    public ResultHandler createGetChatMessagesFailDocument() {
        return restDocs.document(
                pathParameters(
                        parameterWithName("id").description("채팅방 아이디")
                ),
                responseFields(
                        createResponseResultDescriptor()
                )
        );
    }

    public ResultHandler createDeleteChatRoomDocument() {
        return restDocs.document(
                pathParameters(
                        parameterWithName("id").description("채팅방 아이디")
                ),
                responseFields(
                        createResponseResultDescriptor()
                )
        );
    }

    public ResultHandler createResponseResultDocument() {
        return createResponseResultDocument(restDocs);
    }
}
