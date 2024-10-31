package com.carrot.carrotmarketclonecoding.board.helper.searchkeyword;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

import com.carrot.carrotmarketclonecoding.util.RestDocsHelper;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.request.QueryParametersSnippet;
import org.springframework.test.web.servlet.ResultHandler;

@TestComponent
public class SearchKeywordRestDocsHelper extends RestDocsHelper {

    private final RestDocumentationResultHandler restDocs;

    public SearchKeywordRestDocsHelper(RestDocumentationResultHandler restDocs) {
        this.restDocs = restDocs;
    }

    public ResultHandler createSearchKeywordSuccessDocument() {
        return restDocs.document(
                responseFields(
                        fieldWithPath("status").description("응답 상태"),
                        fieldWithPath("result").description("응답 결과"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data[]").description("키워드 목록")
                )
        );
    }

    public ResultHandler createRemoveRecentSearchKeywordDocument() {
        return restDocs.document(
                documentRemoveRecentSearchKeywordsQueryParameters(),
                responseFields(createResponseResultDescriptor())
        );
    }

    private QueryParametersSnippet documentRemoveRecentSearchKeywordsQueryParameters() {
        return queryParameters(
                parameterWithName("keyword").description("키워드명")
        );
    }

    public ResultHandler createResponseResultDocument() {
        return createResponseResultDocument(restDocs);
    }
}
