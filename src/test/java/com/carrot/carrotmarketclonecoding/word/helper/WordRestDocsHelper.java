package com.carrot.carrotmarketclonecoding.word.helper;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

import com.carrot.carrotmarketclonecoding.util.RestDocsHelper;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.ResultHandler;

public class WordRestDocsHelper extends RestDocsHelper {

    private final RestDocumentationResultHandler restDocs;

    public WordRestDocsHelper(RestDocumentationResultHandler restDocs) {
        this.restDocs = restDocs;
    }

    public ResultHandler createAddWordSuccessDocument() {
        return restDocs.document(
                requestFields(
                        getAddWordRequestFieldsDescriptor()
                ),
                responseFields(
                        createResponseResultDescriptor()
                )
        );
    }

    public ResultHandler createInputNotValidDocument() {
        return restDocs.document(
                requestFields(
                        getAddWordRequestFieldsDescriptor()
                ),
                responseFields(
                        addFieldsWithCommonFieldsDescriptors(
                                getInputNotValidFieldDescriptor()
                        )
                )
        );
    }

    public ResultHandler createUpdateAndRemoveWordDocument() {
        return restDocs.document(
                pathParameters(
                        parameterWithName("id").description("수정할 문구 아이디")
                ),
                responseFields(
                        createResponseResultDescriptor()
                )
        );
    }

    private FieldDescriptor[] getInputNotValidFieldDescriptor() {
        return new FieldDescriptor[] {
                fieldWithPath("data.word").description("자주쓰는문구를 입력하지 않음")
        };
    }

    private FieldDescriptor[] getAddWordRequestFieldsDescriptor() {
        return new FieldDescriptor[] {
                fieldWithPath("word").description("자주쓰는문구 내용")
        };
    }

    public ResultHandler createGetWordsSuccessDocument() {
        return restDocs.document(
                responseFields(
                        addFieldsWithCommonFieldsDescriptors(
                                getWordResponseFieldsDescriptor()
                        )
                )
        );
    }

    private FieldDescriptor[] getWordResponseFieldsDescriptor() {
        return new FieldDescriptor[] {
                fieldWithPath("data[].id").description("자주쓰는문구 아이디"),
                fieldWithPath("data[].word").description("자주쓰는문구 내용")
        };
    }

    public ResultHandler createResponseResultDocument() {
        return createResponseResultDocument(restDocs);
    }
}
