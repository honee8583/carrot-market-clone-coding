package com.carrot.carrotmarketclonecoding.notification.helper;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

import com.carrot.carrotmarketclonecoding.util.RestDocsHelper;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.ResultHandler;

public class NotificationRestDocsHelper extends RestDocsHelper {

    private final RestDocumentationResultHandler restDocs;

    public NotificationRestDocsHelper(RestDocumentationResultHandler restDocs) {
        this.restDocs = restDocs;
    }

    public ResultHandler createConnectSuccessDocument() {
        return restDocs.document(
                requestHeaders(
                        headerWithName("lastEventId").optional().description("마지막 수신 알림(SseEmitter) 아이디")
                )
        );
    }

    public ResultHandler createGetAllNotificationSuccessDocument() {
        return restDocs.document(
                responseFields(
                        addFieldsWithCommonFieldsDescriptors(createNotificationResponseDtoDescriptors())
                )
        );
    }

    private FieldDescriptor[] createNotificationResponseDtoDescriptors() {
        return new FieldDescriptor[]{
                fieldWithPath("data[].id").description("알림 아이디"),
                fieldWithPath("data[].content").description("알림 내용"),
                fieldWithPath("data[].type").description("알림 종류"),
                fieldWithPath("data[].isRead").description("읽음여부"),
                fieldWithPath("data[].createDate").description("생성일"),
                fieldWithPath("data[].updateDate").description("수정일")
        };
    }

    public ResultHandler createReadNotificationDocument() {
        return restDocs.document(
                pathParameters(
                        parameterWithName("id").description("알림 아이디")
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
