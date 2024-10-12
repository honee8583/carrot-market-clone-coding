package com.carrot.carrotmarketclonecoding.util;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class ControllerTestUtil {

    protected MockHttpServletRequestBuilder requestWithCsrf(MockHttpServletRequestBuilder requestBuilder, MediaType contentType) {
        if (contentType != null) {
            requestBuilder.contentType(contentType);
        }
        return requestBuilder.with(SecurityMockMvcRequestPostProcessors.csrf());
    }

    protected ResultActions assertResult(ResultActions resultActions, ResultMatcher resultMatcher, int status, boolean result, String message, String dataExpression, Object data) throws Exception {
        return resultActions
                .andExpect(resultMatcher)
                .andExpect(jsonPath("$.status", equalTo(status)))
                .andExpect(jsonPath("$.result", equalTo(result)))
                .andExpect(jsonPath("$.message", equalTo(message)))
                .andExpect(jsonPath(dataExpression, equalTo(data)));
    }
}
