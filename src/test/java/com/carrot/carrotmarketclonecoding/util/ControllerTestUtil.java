package com.carrot.carrotmarketclonecoding.util;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

public class ControllerTestUtil {

    protected MockMultipartHttpServletRequestBuilder requestMultipartFiles(MockMultipartHttpServletRequestBuilder requestBuilder,
                                                                         MockMultipartFile[] pictures) {
        for (MockMultipartFile picture : pictures) {
            requestBuilder.file(picture);
        }
        return requestBuilder;
    }

    protected MockHttpServletRequestBuilder requestWithCsrfAndSetContentType(MockHttpServletRequestBuilder requestBuilder, MediaType contentType) {
        return requestWithCsrf(requestBuilder).contentType(contentType);
    }

    protected MockHttpServletRequestBuilder requestWithCsrf(MockHttpServletRequestBuilder requestBuilder) {
        return requestBuilder.with(SecurityMockMvcRequestPostProcessors.csrf());
    }

    protected ResultActions assertResponseResult(ResultActions resultActions, ResultFields resultFields) throws Exception {
        return resultActions
                .andExpect(resultFields.getResultMatcher())
                .andExpect(jsonPath("$.status", equalTo(resultFields.getStatus())))
                .andExpect(jsonPath("$.result", equalTo(resultFields.isResult())))
                .andExpect(jsonPath("$.message", equalTo(resultFields.getMessage())));
    }
}
