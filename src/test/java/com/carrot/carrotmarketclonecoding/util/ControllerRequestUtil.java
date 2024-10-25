package com.carrot.carrotmarketclonecoding.util;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

public class ControllerRequestUtil {

    protected MockHttpServletRequestBuilder requestWithCsrfAndSetContentType(MockHttpServletRequestBuilder requestBuilder,
                                                                             MediaType contentType) {
        return requestWithCsrf(requestBuilder).contentType(contentType);
    }

    protected MockHttpServletRequestBuilder requestWithCsrf(MockHttpServletRequestBuilder requestBuilder) {
        return requestBuilder.with(SecurityMockMvcRequestPostProcessors.csrf());
    }

    protected MockMultipartHttpServletRequestBuilder requestWithMultipartFiles(
            MockMultipartHttpServletRequestBuilder requestBuilder,
            MockMultipartFile[] files) {
        for (MockMultipartFile file : files) {
            requestBuilder.file(file);
        }
        return requestBuilder;
    }
}
