package com.carrot.carrotmarketclonecoding.keyword.helper;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

import com.carrot.carrotmarketclonecoding.util.RestDocsHelper;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.ResultHandler;

public class KeywordRestDocsHelper extends RestDocsHelper {

    private final RestDocumentationResultHandler restDocs;

    public KeywordRestDocsHelper(RestDocumentationResultHandler restDocs) {
        this.restDocs = restDocs;
    }

    public ResultHandler createAddKeywordDocument() {
        return restDocs.document(
            requestFields(
                    fieldWithPath("name").description("키워드명")
            ),
            responseFields(
                    createResponseResultDescriptor()
            )
        );
    }

    public ResultHandler createGetKeywordsSuccessDocument() {
        return restDocs.document(
                responseFields(
                        fieldWithPath("status").description("응답 상태"),
                        fieldWithPath("result").description("응답 결과"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data[].id").description("키워드 아이디"),
                        fieldWithPath("data[].categoryId").description("카테고리 아이디"),
                        fieldWithPath("data[].name").description("키워드명"),
                        fieldWithPath("data[].minPrice").description("최소금액"),
                        fieldWithPath("data[].maxPrice").description("최대금액"),
                        fieldWithPath("data[].createDate").description("생성일"),
                        fieldWithPath("data[].updateDate").description("수정일")
                )
        );
    }

    public ResultHandler createEditKeywordDocument() {
        return restDocs.document(
                pathParameters(
                        parameterWithName("id").description("수정할 키워드의 아이디")
                ),
                requestFields(
                        fieldWithPath("name").description("수정할 키워드명"),
                        fieldWithPath("categoryId").description("수정할 카테고리"),
                        fieldWithPath("minPrice").description("수정할 최소금액"),
                        fieldWithPath("maxPrice").description("수정할 최대금액")
                ),
                responseFields(
                        createResponseResultDescriptor()
                )
        );
    }

    public ResultHandler createDeleteKeywordDocument() {
        return restDocs.document(
                pathParameters(
                        parameterWithName("id").description("삭제할 키워드의 아이디")
                ),
                responseFields(
                        createResponseResultDescriptor()
                )
        );
    }

    // TODO createCommonDocument() -> 중복제거 (다른 DocsHelper 클래스에서도 중복되는 메서드)
    public ResultHandler createCommonDocument() {
        return createResponseResultDocument(restDocs);
    }
}
