package com.carrot.carrotmarketclonecoding.word.controller;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.INPUT_NOT_VALID;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.MEMBER_NOT_FOUND;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.MEMBER_WORD_OVER_LIMIT;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.ADD_WORD_SUCCESS;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.GET_MEMBER_WORDS;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberWordLimitException;
import com.carrot.carrotmarketclonecoding.word.dto.WordRequestDto.WordRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.word.dto.WordResponseDto.WordListResponseDto;
import com.carrot.carrotmarketclonecoding.word.dto.validation.WordRegisterValidationMessage.MESSAGE;
import com.carrot.carrotmarketclonecoding.word.service.impl.WordServiceImpl;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(WordController.class)
class WordControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private WordServiceImpl wordService;

    @Nested
    @DisplayName("자주쓰는문구 추가 컨트롤러 테스트")
    class AddWord {

        @Test
        @DisplayName("성공")
        void addWordSuccess() throws Exception {
            // given
            // when
            doNothing().when(wordService).add(anyLong(), any());

            // then
            mvc.perform(post("/word")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(new WordRegisterRequestDto("word"))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status", equalTo(201)))
                    .andExpect(jsonPath("$.result", equalTo(true)))
                    .andExpect(jsonPath("$.message", equalTo(ADD_WORD_SUCCESS.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName("실패 - 유효성 검사 실패")
        void addWordFailNotValid() throws Exception {
            // given
            Map<String, String> map = new HashMap<>();
            map.put("word", MESSAGE.WORD_NOT_VALID);

            // when
            // then
            mvc.perform(post("/word")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(new WordRegisterRequestDto())))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(INPUT_NOT_VALID.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(map)));
        }

        @Test
        @DisplayName("실패 - 사용자가 존재하지 않음")
        void addWordFailMemberNotFound() throws Exception {
            // given

            // when
            doThrow(MemberNotFoundException.class).when(wordService).add(anyLong(), any());

            // then
            mvc.perform(post("/word")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(new WordRegisterRequestDto("word"))))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status", equalTo(401)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(MEMBER_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName("실패 - 자주쓰는문구의 개수가 30개를 초과함")
        void addWordFailMemberWordOverLimit() throws Exception {
            // given
            // when
            doThrow(MemberWordLimitException.class).when(wordService).add(anyLong(), any());

            // then
            mvc.perform(post("/word")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(new WordRegisterRequestDto("word"))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(MEMBER_WORD_OVER_LIMIT.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }
    }

    @Nested
    @DisplayName("자주쓰는문구 목록 조회 컨트롤러 테스트")
    class WordList {

        @Test
        @DisplayName("성공")
        void getWordsSuccess() throws Exception {
            // given
            List<WordListResponseDto> words = Arrays.asList(
                    new WordListResponseDto(1L, "word1"),
                    new WordListResponseDto(2L, "word2")
            );

            // when
            when(wordService.list(anyLong())).thenReturn(words);

            // then
            mvc.perform(get("/word"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", equalTo(200)))
                    .andExpect(jsonPath("$.result", equalTo(true)))
                    .andExpect(jsonPath("$.message", equalTo(GET_MEMBER_WORDS.getMessage())))
                    .andExpect(jsonPath("$.data.size()", equalTo(2)));
        }

        @Test
        @DisplayName("실패 - 사용자가 존재하지 않음")
        void getWordsFailMemberNotFound() throws Exception {
            // given
            // when
            doThrow(MemberNotFoundException.class).when(wordService).list(anyLong());

            // then
            mvc.perform(get("/word"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status", equalTo(401)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(MEMBER_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }
    }
}