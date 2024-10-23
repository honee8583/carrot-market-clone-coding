package com.carrot.carrotmarketclonecoding.util;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.restdocs.payload.FieldDescriptor;

@TestComponent
public class RestDocsHelper {

    public FieldDescriptor[] createPageFieldsDescriptor(FieldDescriptor[] contentsDescriptors) {
        List<FieldDescriptor> fieldDescriptors = new ArrayList<>(Arrays.asList(
                fieldWithPath("status").description("응답 상태"),
                fieldWithPath("result").description("응답 결과"),
                fieldWithPath("message").description("응답 메시지"),
                fieldWithPath("data.totalPage").description("전체 페이지 개수"),
                fieldWithPath("data.totalElements").description("전체 데이터 수"),
                fieldWithPath("data.first").description("첫페이지 여부"),
                fieldWithPath("data.last").description("마지막페이지 여부"),
                fieldWithPath("data.numberOfElements").description("현재 페이지 데이터 개수")
        ));
        fieldDescriptors.addAll(Arrays.asList(contentsDescriptors));
        return fieldDescriptors.toArray(new FieldDescriptor[0]);
    }

    public FieldDescriptor[] createResponseResultDescriptor() {
        return new FieldDescriptor[] {
                fieldWithPath("status").description("응답 상태"),
                fieldWithPath("result").description("응답 결과"),
                fieldWithPath("message").description("응답 메시지"),
                fieldWithPath("data").description("응답 본문")
        };
    }
}
