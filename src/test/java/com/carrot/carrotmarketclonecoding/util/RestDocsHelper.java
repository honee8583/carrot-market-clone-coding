package com.carrot.carrotmarketclonecoding.util;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.ResultHandler;

@TestComponent
public class RestDocsHelper {

    protected ResultHandler createResponseResultDocument(RestDocumentationResultHandler restDocs) {
        return restDocs.document(
                responseFields(createResponseResultDescriptor())
        );
    }

    public FieldDescriptor[] createPageFieldsDescriptor(FieldDescriptor[] contentsDescriptors) {
        return addDescriptors(
                addDescriptors(getCommonFieldDescriptor(), getCommonPageFieldsDescriptors()),
                contentsDescriptors
        );
    }

    private FieldDescriptor[] getCommonPageFieldsDescriptors() {
        return new FieldDescriptor[] {
                fieldWithPath("data.totalPage").description("전체 페이지 개수"),
                fieldWithPath("data.totalElements").description("전체 데이터 수"),
                fieldWithPath("data.first").description("첫페이지 여부"),
                fieldWithPath("data.last").description("마지막페이지 여부"),
                fieldWithPath("data.numberOfElements").description("현재 페이지 데이터 개수")
        };
    }

    public FieldDescriptor[] createResponseResultDescriptor() {
        return addFieldsWithCommonFieldsDescriptors(new FieldDescriptor[]{
                fieldWithPath("data").description("응답 본문")
        });
    }

    public FieldDescriptor[] addFieldsWithCommonFieldsDescriptors(FieldDescriptor[] descriptors) {
        return addDescriptors(getCommonFieldDescriptor(), descriptors);
    }

    private FieldDescriptor[] getCommonFieldDescriptor() {
        return new FieldDescriptor[]{
                fieldWithPath("status").description("응답 상태"),
                fieldWithPath("result").description("응답 결과"),
                fieldWithPath("message").description("응답 메시지")
        };
    }

    private FieldDescriptor[] addDescriptors(FieldDescriptor[] fd1,
                                             FieldDescriptor[] fd2) {
        List<FieldDescriptor> fieldDescriptors = new ArrayList<>(Arrays.asList(fd1));
        fieldDescriptors.addAll(Arrays.asList(fd2));
        return fieldDescriptors.toArray(new FieldDescriptor[0]);
    }
}
