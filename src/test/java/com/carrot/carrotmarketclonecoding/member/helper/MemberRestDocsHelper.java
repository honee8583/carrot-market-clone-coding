package com.carrot.carrotmarketclonecoding.member.helper;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;

import com.carrot.carrotmarketclonecoding.util.RestDocsHelper;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.ResultHandler;

public class MemberRestDocsHelper extends RestDocsHelper {

    private final RestDocumentationResultHandler restDocs;

    public MemberRestDocsHelper(RestDocumentationResultHandler restDocs) {
        this.restDocs = restDocs;
    }

    public ResultHandler createUpdateProfileSuccessDocument() {
        return restDocs.document(
                requestParts(
                        partWithName("profileImage").description("수정할 프로필 사진"),
                        partWithName("profileUpdateRequest").description("수정할 프로필 정보")
                ),
                requestPartBody("profileUpdateRequest"),
                requestPartFields("profileUpdateRequest",
                        fieldWithPath("nickname").description("수정할 닉네임")
                ),
                responseFields(createResponseResultDescriptor())
        );
    }

    public ResultHandler createResponseResultDocument() {
        return createResponseResultDocument(restDocs);
    }

    public ResultHandler createGetProfileDetailSuccessDocument() {
        return restDocs.document(
                responseFields(
                        fieldWithPath("status").description("응답 상태"),
                        fieldWithPath("result").description("응답 결과"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data.profileUrl").description("사용자의 프로필 URL"),
                        fieldWithPath("data.nickname").description("사용자의 닉네임")
                )
        );
    }
}
