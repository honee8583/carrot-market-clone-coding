package com.carrot.carrotmarketclonecoding.category.helper.category;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.carrot.carrotmarketclonecoding.util.RestDocsHelper;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.ResultHandler;

public class CategoryRestDocsHelper extends RestDocsHelper {

    private final RestDocumentationResultHandler restDocs;

    public CategoryRestDocsHelper(RestDocumentationResultHandler restDocs) {
        this.restDocs = restDocs;
    }

    public ResultHandler createGetAllCategoriesDocument() {
        return restDocs.document(
                responseFields(
                        fieldWithPath("status").description("응답 상태"),
                        fieldWithPath("result").description("응답 결과"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data[]").description("응답 본문"),
                        fieldWithPath("data[].id").description("카테고리 아이디"),
                        fieldWithPath("data[].name").description("카테고리 이름")
                )
        );
    }
}
