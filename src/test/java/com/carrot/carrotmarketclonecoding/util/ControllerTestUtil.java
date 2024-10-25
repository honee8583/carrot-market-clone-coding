package com.carrot.carrotmarketclonecoding.util;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.test.web.servlet.ResultActions;

public class ControllerTestUtil {

    protected ResultActions assertResponseResult(ResultActions resultActions, ResultFields resultFields) throws Exception {
        return resultActions
                .andExpect(resultFields.getResultMatcher())
                .andExpect(jsonPath("$.status", equalTo(resultFields.getStatus())))
                .andExpect(jsonPath("$.result", equalTo(resultFields.isResult())))
                .andExpect(jsonPath("$.message", equalTo(resultFields.getMessage())));
    }
}
