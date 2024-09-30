package com.carrot.carrotmarketclonecoding.keyword.controller;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.KEYWORD_OVER_LIMIT;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.MEMBER_NOT_FOUND;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.ADD_KEYWORD_SUCCESS;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrot.carrotmarketclonecoding.auth.config.WithCustomMockUser;
import com.carrot.carrotmarketclonecoding.common.exception.KeywordOverLimitException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.keyword.dto.KeywordRequestDto.KeywordCreateRequestDto;
import com.carrot.carrotmarketclonecoding.keyword.service.impl.KeywordServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@WithCustomMockUser
@WebMvcTest(controllers = KeywordController.class)
class KeywordControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private KeywordServiceImpl keywordService;

    @Nested
    @DisplayName("키워드 추가 컨트롤러 테스트")
    class Add {

        @Test
        @DisplayName("성공")
        void addSuccess() throws Exception {
            // given
            // when
            doNothing().when(keywordService).add(anyLong(), any(KeywordCreateRequestDto.class));

            // then
            mvc.perform(post("/keyword")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(mock(KeywordCreateRequestDto.class)))
                            .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status", equalTo(201)))
                    .andExpect(jsonPath("$.result", equalTo(true)))
                    .andExpect(jsonPath("$.message", equalTo(ADD_KEYWORD_SUCCESS.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName("존재하지 않는 사용자일 경우 401 반환")
        void addFailMemberNotFound() throws Exception {
            // given
            // when
            doThrow(MemberNotFoundException.class).when(keywordService).add(anyLong(), any(KeywordCreateRequestDto.class));

            // then
            mvc.perform(post("/keyword")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(mock(KeywordCreateRequestDto.class)))
                            .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status", equalTo(401)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(MEMBER_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName("사용자의 키워드 개수가 30개를 넘어갈경우 400 반환")
        void addFailKeywordOverLimit() throws Exception {
            // given
            // when
            doThrow(KeywordOverLimitException.class).when(keywordService).add(anyLong(), any(KeywordCreateRequestDto.class));

            // then
            mvc.perform(post("/keyword")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(mock(KeywordCreateRequestDto.class)))
                            .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(KEYWORD_OVER_LIMIT.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }
    }

    @Test
    @DisplayName("사용자의 키워드 목록 조회 성공 컨트롤러 테스트")
    void getKeywordsSuccess() {
        // given
        // when
        // then
    }

    @Test
    @DisplayName("존재하지 않는 사용자일 경우 403 반환")
    void getKeywordsFailMemberNotFound() {
        // given
        // when
        // then
    }

    @Test
    @DisplayName("키워드 편집 성공 컨트롤러 테스트")
    void editSuccess() {
        // given
        // when
        // then
    }

    @Test
    @DisplayName("사용자가 존재하지 않을 경우 403 반환")
    void editFailMemberNotFound() {
        // given
        // when
        // then
    }

    @Test
    @DisplayName("편집할 키워드가 존재하지 않을 경우 404 반환")
    void editFailKeywordNotFound() {
        // given
        // when
        // then
    }

    @Test
    @DisplayName("저장할 카테고리가 존재하지 않을 경우 404 반환")
    void editFailCategoryNotFound() {
        // given
        // when
        // then
    }

    @Test
    @DisplayName("키워드 삭제 성공 컨트롤러 테스트")
    void deleteSuccess() {
        // given
        // when
        // then
    }

    @Test
    @DisplayName("사용자가 존재하지 않을 경우 403 반환")
    void deleteFailMemberNotFound() {
        // given
        // when
        // then
    }

    @Test
    @DisplayName("삭제할 키워드가 존재하지 않을 경우 404 반환")
    void deleteFailKeywordNotFound() {
        // given
        // when
        // then
    }
}