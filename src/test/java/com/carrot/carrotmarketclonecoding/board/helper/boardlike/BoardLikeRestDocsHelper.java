package com.carrot.carrotmarketclonecoding.board.helper.boardlike;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

import com.carrot.carrotmarketclonecoding.util.RestDocsHelper;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.ResultHandler;

@TestComponent
public class BoardLikeRestDocsHelper extends RestDocsHelper {

    private final RestDocumentationResultHandler restDocs;

    public BoardLikeRestDocsHelper(RestDocumentationResultHandler restDocs) {
        this.restDocs = restDocs;
    }

    public RestDocumentationResultHandler createAddBoardLikeDocument() {
        return restDocs.document(
                pathParameters(parameterWithName("id").description("게시글 ID")),
                responseFields(createResponseResultDescriptor())
        );
    }

    public ResultHandler createGetUserLikedBoardsSuccessDocument() {
        return restDocs.document(
                queryParameters(parameterWithName("page").description("페이지번호")),
                responseFields(createPageFieldsDescriptor(createContentsFieldDescriptor()))
        );
    }

    private FieldDescriptor[] createContentsFieldDescriptor() {
        return new FieldDescriptor[] {
                fieldWithPath("data.contents[].id").description("게시글 아이디"),
                fieldWithPath("data.contents[].pictureUrl").description("사진 URL"),
                fieldWithPath("data.contents[].title").description("게시글 제목"),
                fieldWithPath("data.contents[].place").description("거래 장소"),
                fieldWithPath("data.contents[].createDate").description("게시글 생성일"),
                fieldWithPath("data.contents[].price").description("상품 가격"),
                fieldWithPath("data.contents[].like").description("좋아요 수")
        };
    }

    public ResultHandler createGetUserLikedBoardsFailedDocument() {
        return restDocs.document(responseFields(createResponseResultDescriptor()));
    }
}
