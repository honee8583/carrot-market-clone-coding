package com.carrot.carrotmarketclonecoding.word.controller;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.*;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.*;
import static com.carrot.carrotmarketclonecoding.word.WordTestDisplayNames.MESSAGE.*;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberWordLimitException;
import com.carrot.carrotmarketclonecoding.common.exception.WordNotFoundException;
import com.carrot.carrotmarketclonecoding.word.dto.WordRequestDto;
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
    @DisplayName(WORD_ADD_CONTROLLER_TEST)
    class AddWord {

        @Test
        @DisplayName(SUCCESS)
        void addWordSuccess() throws Exception {
            // given
            // when
            doNothing().when(wordService).add(anyLong(), any());

            // then
            mvc.perform(post("/word")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(new WordRequestDto("word"))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status", equalTo(201)))
                    .andExpect(jsonPath("$.result", equalTo(true)))
                    .andExpect(jsonPath("$.message", equalTo(ADD_WORD_SUCCESS.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName(FAIL_INPUT_NOT_VALID)
        void addWordFailNotValid() throws Exception {
            // given
            Map<String, String> map = new HashMap<>();
            map.put("word", MESSAGE.WORD_NOT_VALID);

            // when
            // then
            mvc.perform(post("/word")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(new WordRequestDto())))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(INPUT_NOT_VALID.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(map)));
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void addWordFailMemberNotFound() throws Exception {
            // given

            // when
            doThrow(MemberNotFoundException.class).when(wordService).add(anyLong(), any());

            // then
            mvc.perform(post("/word")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(new WordRequestDto("word"))))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status", equalTo(401)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(MEMBER_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName(FAIL_MEMBER_WORD_OVER_LIMIT)
        void addWordFailMemberWordOverLimit() throws Exception {
            // given
            // when
            doThrow(MemberWordLimitException.class).when(wordService).add(anyLong(), any());

            // then
            mvc.perform(post("/word")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(new WordRequestDto("word"))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(MEMBER_WORD_OVER_LIMIT.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }
    }

    @Nested
    @DisplayName(WORD_LIST_CONTROLLER_TEST)
    class WordList {

        @Test
        @DisplayName(SUCCESS)
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
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
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

    @Nested
    @DisplayName(WORD_UPDATE_CONTROLLER_TEST)
    class UpdateWord {

        @Test
        @DisplayName(SUCCESS)
        void updateWordSuccess() throws Exception {
            // given
            // when
            doNothing().when(wordService).update(anyLong(), anyLong(), any());

            // then
            mvc.perform(put("/word/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(new WordRequestDto("word"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", equalTo(200)))
                    .andExpect(jsonPath("$.result", equalTo(true)))
                    .andExpect(jsonPath("$.message", equalTo(UPDATE_WORD_SUCCESS.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void updateWordFailMemberNotFound() throws Exception {
            // given
            // when
            doThrow(MemberNotFoundException.class).when(wordService).update(anyLong(), anyLong(), any());

            // then
            mvc.perform(put("/word/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(new WordRequestDto("word"))))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status", equalTo(401)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(MEMBER_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName(FAIL_WORD_NOT_FOUND)
        void updateWordFailWordNotFound() throws Exception {
            // given
            // when
            doThrow(WordNotFoundException.class).when(wordService).update(anyLong(), anyLong(), any());

            // then
            mvc.perform(put("/word/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(new WordRequestDto("word"))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(WORD_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }
    }

    @Nested
    @DisplayName(WORD_REMOVE_CONTROLLER_TEST)
    class RemoveWord {

        @Test
        @DisplayName(SUCCESS)
        void removeWordSuccess() throws Exception {
            // given

            // when
            doNothing().when(wordService).remove(anyLong(), anyLong());

            // then
            mvc.perform(delete("/word/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", equalTo(200)))
                    .andExpect(jsonPath("$.result", equalTo(true)))
                    .andExpect(jsonPath("$.message", equalTo(REMOVE_WORD_SUCCESS.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void removeWordFailMemberNotFound() throws Exception {
            // given
            // when
            doThrow(MemberNotFoundException.class).when(wordService).remove(anyLong(), anyLong());

            // then
            mvc.perform(delete("/word/{id}", 1L))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status", equalTo(401)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(MEMBER_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName(FAIL_WORD_NOT_FOUND)
        void removeWordFailWordNotFound() throws Exception {
            // given
            // when
            doThrow(WordNotFoundException.class).when(wordService).remove(anyLong(), anyLong());

            // then
            mvc.perform(delete("/word/{id}", 1L))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(WORD_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }
    }
}