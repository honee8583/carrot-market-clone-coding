package com.carrot.carrotmarketclonecoding.keyword.controller;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.*;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.*;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrot.carrotmarketclonecoding.auth.config.WithCustomMockUser;
import com.carrot.carrotmarketclonecoding.common.exception.CategoryNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.KeywordNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.KeywordOverLimitException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.UnauthorizedAccessException;
import com.carrot.carrotmarketclonecoding.keyword.dto.KeywordRequestDto.KeywordCreateRequestDto;
import com.carrot.carrotmarketclonecoding.keyword.dto.KeywordRequestDto.KeywordEditRequestDto;
import com.carrot.carrotmarketclonecoding.keyword.dto.KeywordResponseDto.KeywordDetailResponseDto;
import com.carrot.carrotmarketclonecoding.keyword.service.impl.KeywordServiceImpl;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
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
            doNothing().when(keywordService).add(anyLong(), any(KeywordCreateRequestDto.class));

            // when
            ResultActions resultActions = mvc.perform(post("/keyword")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(mock(KeywordCreateRequestDto.class)))
                    .with(SecurityMockMvcRequestPostProcessors.csrf()));

            // then
            assertResponseResult(resultActions, status().isCreated(), 201, true, ADD_KEYWORD_SUCCESS.getMessage(), "$.data", null);
        }

        @Test
        @DisplayName("존재하지 않는 사용자일 경우 401 반환")
        void addFailMemberNotFound() throws Exception {
            // given
            doThrow(MemberNotFoundException.class).when(keywordService).add(anyLong(), any(KeywordCreateRequestDto.class));

            // when
            ResultActions resultActions = mvc.perform(post("/keyword")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(mock(KeywordCreateRequestDto.class)))
                    .with(SecurityMockMvcRequestPostProcessors.csrf()));

            // then
            assertResponseResult(resultActions, status().isUnauthorized(), 401, false, MEMBER_NOT_FOUND.getMessage(), "$.data", null);
        }

        @Test
        @DisplayName("사용자의 키워드 개수가 30개를 넘어갈경우 400 반환")
        void addFailKeywordOverLimit() throws Exception {
            // given
            doThrow(KeywordOverLimitException.class).when(keywordService).add(anyLong(), any(KeywordCreateRequestDto.class));

            // when
            ResultActions resultActions = mvc.perform(post("/keyword")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(mock(KeywordCreateRequestDto.class)))
                    .with(SecurityMockMvcRequestPostProcessors.csrf()));

            // then
            assertResponseResult(resultActions, status().isBadRequest(), 400, false, KEYWORD_OVER_LIMIT.getMessage(),"$.data", null);
        }
    }

    @Nested
    @DisplayName("사용자의 키워드 목록 조회 컨트롤러 테스트")
    class GetKeywords {

        @Test
        @DisplayName("성공")
        void getKeywordsSuccess() throws Exception {
            // given
            List<KeywordDetailResponseDto> keywords = Arrays.asList(
                    new KeywordDetailResponseDto(),
                    new KeywordDetailResponseDto()
            );
            when(keywordService.getAllKeywords(anyLong())).thenReturn(keywords);

            // when
            ResultActions resultActions = mvc.perform(get("/keyword"));

            // then
            assertResponseResult(resultActions, status().isOk(), 200, true, GET_KEYWORDS_SUCCESS.getMessage(), "$.data.size()", 2);
        }

        @Test
        @DisplayName("존재하지 않는 사용자일 경우 401 반환")
        void getKeywordsFailMemberNotFound() throws Exception {
            // given
            doThrow(MemberNotFoundException.class).when(keywordService).getAllKeywords(anyLong());

            // when
            ResultActions resultActions = mvc.perform(get("/keyword"));

            // then
            assertResponseResult(resultActions, status().isUnauthorized(), 401, false, MEMBER_NOT_FOUND.getMessage(), "$.data", null);
        }
    }

    @Nested
    @DisplayName("키워드 편집 컨트롤러 테스트")
    class Edit {

        @Test
        @DisplayName("성공")
        void editSuccess() throws Exception {
            // given
            doNothing().when(keywordService).edit(anyLong(), anyLong(), any(KeywordEditRequestDto.class));

            // when
            ResultActions resultActions = mvc.perform(patch("/keyword/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(mock(KeywordEditRequestDto.class)))
                    .with(SecurityMockMvcRequestPostProcessors.csrf()));

            // then
            assertResponseResult(resultActions, status().isOk(), 200, true, EDIT_KEYWORD_SUCCESS.getMessage(), "$.data", null);
        }

        @Test
        @DisplayName("사용자가 존재하지 않을 경우 401 반환")
        void editFailMemberNotFound() throws Exception {
            // given
            doThrow(MemberNotFoundException.class).when(keywordService)
                    .edit(anyLong(), anyLong(), any(KeywordEditRequestDto.class));

            // when
            ResultActions resultActions = mvc.perform(patch("/keyword/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(mock(KeywordEditRequestDto.class)))
                    .with(SecurityMockMvcRequestPostProcessors.csrf()));

            // then
            assertResponseResult(resultActions, status().isUnauthorized(), 401, false, MEMBER_NOT_FOUND.getMessage(), "$.data", null);
        }

        @Test
        @DisplayName("편집할 키워드가 존재하지 않을 경우 400 반환")
        void editFailKeywordNotFound() throws Exception {
            // given
            doThrow(KeywordNotFoundException.class).when(keywordService)
                    .edit(anyLong(), anyLong(), any(KeywordEditRequestDto.class));

            // when
            ResultActions resultActions = mvc.perform(patch("/keyword/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(mock(KeywordEditRequestDto.class)))
                            .with(SecurityMockMvcRequestPostProcessors.csrf()));

                    // then
            assertResponseResult(resultActions, status().isBadRequest(), 400, false, KEYWORD_NOT_FOUND.getMessage(), "$.data", null);
        }

        @Test
        @DisplayName("편집할 키워드가 사용자의 키워드가 아닐 경우 400 반환")
        void editFailNotKeywordMember() throws Exception {
            // given
            doThrow(UnauthorizedAccessException.class).when(keywordService)
                    .edit(anyLong(), anyLong(), any(KeywordEditRequestDto.class));

            // when
            ResultActions resultActions = mvc.perform(patch("/keyword/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(mock(KeywordEditRequestDto.class)))
                    .with(SecurityMockMvcRequestPostProcessors.csrf()));

            // then
            assertResponseResult(resultActions, status().isForbidden(), 403, false, UNAUTHORIZED_ACCESS.getMessage(),"$.data", null);
        }

        @Test
        @DisplayName("저장할 카테고리가 존재하지 않을 경우 400 반환")
        void editFailCategoryNotFound() throws Exception {
            // given
            doThrow(CategoryNotFoundException.class).when(keywordService)
                    .edit(anyLong(), anyLong(), any(KeywordEditRequestDto.class));

            // when
            ResultActions resultActions = mvc.perform(patch("/keyword/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(mock(KeywordEditRequestDto.class)))
                    .with(SecurityMockMvcRequestPostProcessors.csrf()));

            // then
            assertResponseResult(resultActions, status().isBadRequest(), 400, false, CATEGORY_NOT_FOUND.getMessage(), "$.data", null);
        }
    }

    @Nested
    @DisplayName("키워드 삭제 컨트롤러 테스트")
    class Delete {

        @Test
        @DisplayName("성공")
        void deleteSuccess() throws Exception {
            // given
            doNothing().when(keywordService).delete(anyLong(), anyLong());

            // when
            ResultActions resultActions = mvc.perform(delete("/keyword/{id}", 1L)
                    .with(SecurityMockMvcRequestPostProcessors.csrf()));

            // then
            assertResponseResult(resultActions, status().isOk(), 200, true, DELETE_KEYWORDS_SUCCESS.getMessage(), "$.data", null);
        }

        @Test
        @DisplayName("사용자가 존재하지 않을 경우 401 반환")
        void deleteFailMemberNotFound() throws Exception {
            // given
            doThrow(MemberNotFoundException.class).when(keywordService).delete(anyLong(), anyLong());

            // when
            ResultActions resultActions = mvc.perform(delete("/keyword/{id}", 1L)
                    .with(SecurityMockMvcRequestPostProcessors.csrf()));

            // then
            assertResponseResult(resultActions, status().isUnauthorized(), 401, false, MEMBER_NOT_FOUND.getMessage(), "$.data", null);
        }

        @Test
        @DisplayName("삭제할 키워드가 존재하지 않을 경우 400 반환")
        void deleteFailKeywordNotFound() throws Exception {
            // given
            doThrow(KeywordNotFoundException.class).when(keywordService).delete(anyLong(), anyLong());

            // when
            ResultActions resultActions = mvc.perform(delete("/keyword/{id}", 1L)
                    .with(SecurityMockMvcRequestPostProcessors.csrf()));

            // then
            assertResponseResult(resultActions, status().isBadRequest(), 400, false, KEYWORD_NOT_FOUND.getMessage(), "$.data", null);
        }
    }

    private void assertResponseResult(ResultActions resultActions,
                                     ResultMatcher resultMatcher,
                                     int status,
                                     boolean result,
                                     String message,
                                     String dataJsonPath,
                                     Object data) throws Exception {
        resultActions.andExpect(resultMatcher)
                .andExpect(jsonPath("$.status", equalTo(status)))
                .andExpect(jsonPath("$.result", equalTo(result)))
                .andExpect(jsonPath("$.message", equalTo(message)))
                .andExpect(jsonPath(dataJsonPath, equalTo(data)));
    }
}