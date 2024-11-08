package com.carrot.carrotmarketclonecoding.word.controller;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.*;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.*;
import static com.carrot.carrotmarketclonecoding.word.displayname.WordTestDisplayNames.MESSAGE.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrot.carrotmarketclonecoding.auth.config.WithCustomMockUser;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberWordLimitException;
import com.carrot.carrotmarketclonecoding.common.exception.WordNotFoundException;
import com.carrot.carrotmarketclonecoding.util.RestDocsTestUtil;
import com.carrot.carrotmarketclonecoding.util.ResultFields;
import com.carrot.carrotmarketclonecoding.word.dto.WordRequestDto;
import com.carrot.carrotmarketclonecoding.word.dto.WordResponseDto.WordListResponseDto;
import com.carrot.carrotmarketclonecoding.word.dto.validation.WordRegisterValidationMessage.MESSAGE;
import com.carrot.carrotmarketclonecoding.word.helper.WordTestHelper;
import com.carrot.carrotmarketclonecoding.word.service.impl.WordServiceImpl;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WithCustomMockUser
@WebMvcTest(controllers = WordController.class)
class WordControllerTest extends RestDocsTestUtil {

    private WordTestHelper testHelper;

    @MockBean
    private WordServiceImpl wordService;

    private WordRequestDto wordRequest;

    @BeforeEach
    void setUp() {
        this.testHelper = new WordTestHelper(mvc, restDocs);
        this.wordRequest = new WordRequestDto("word");
    }

    @Nested
    @DisplayName(WORD_ADD_CONTROLLER_TEST)
    class AddWord {

        @Test
        @DisplayName(SUCCESS)
        void addWordSuccess() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isCreated())
                    .status(201)
                    .result(true)
                    .message(ADD_WORD_SUCCESS.getMessage())
                    .build();

            // when
            doNothing().when(wordService).add(anyLong(), any());

            // then
            testHelper.assertAddWord(resultFields, wordRequest);
        }

        @Test
        @DisplayName(FAIL_INPUT_NOT_VALID)
        void addWordFailNotValid() throws Exception {
            // given
            Map<String, String> map = new HashMap<>();
            map.put("word", MESSAGE.WORD_NOT_VALID);

            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isBadRequest())
                    .status(400)
                    .result(false)
                    .message(INPUT_NOT_VALID.getMessage())
                    .build();

            // when
            // then
            testHelper.assertAddWordInputNotValid(resultFields, new WordRequestDto(), map);
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void addWordFailMemberNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(MemberNotFoundException.class).when(wordService).add(anyLong(), any());

            // then
            testHelper.assertAddWord(resultFields, wordRequest);
        }

        @Test
        @DisplayName(FAIL_MEMBER_WORD_OVER_LIMIT)
        void addWordFailMemberWordOverLimit() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isBadRequest())
                    .status(400)
                    .result(false)
                    .message(MEMBER_WORD_OVER_LIMIT.getMessage())
                    .build();

            // when
            doThrow(MemberWordLimitException.class).when(wordService).add(anyLong(), any());

            // then
            testHelper.assertAddWord(resultFields, wordRequest);
        }
    }

    @Nested
    @DisplayName(WORD_LIST_CONTROLLER_TEST)
    class GetWords {

        @Test
        @DisplayName(SUCCESS)
        void getWordsSuccess() throws Exception {
            // given
            List<WordListResponseDto> words = Arrays.asList(
                    new WordListResponseDto(1L, "word1"),
                    new WordListResponseDto(2L, "word2")
            );

            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(GET_MEMBER_WORDS.getMessage())
                    .build();

            // when
            when(wordService.list(anyLong())).thenReturn(words);

            // then
            testHelper.assertGetWordsSuccess(resultFields);
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void getWordsFailMemberNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build();
            // when
            doThrow(MemberNotFoundException.class).when(wordService).list(anyLong());

            // then
            testHelper.assertGetWordsFail(resultFields);
        }
    }

    @Nested
    @DisplayName(WORD_UPDATE_CONTROLLER_TEST)
    class UpdateWord {

        @Test
        @DisplayName(SUCCESS)
        void updateWordSuccess() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(UPDATE_WORD_SUCCESS.getMessage())
                    .build();

            // when
            doNothing().when(wordService).update(anyLong(), anyLong(), any());

            // then
            testHelper.assertUpdateWord(resultFields, wordRequest);
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void updateWordFailMemberNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(MemberNotFoundException.class).when(wordService).update(anyLong(), anyLong(), any());

            // then
            testHelper.assertUpdateWord(resultFields, wordRequest);
        }

        @Test
        @DisplayName(FAIL_WORD_NOT_FOUND)
        void updateWordFailWordNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isBadRequest())
                    .status(400)
                    .result(false)
                    .message(WORD_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(WordNotFoundException.class).when(wordService).update(anyLong(), anyLong(), any());

            // then
            testHelper.assertUpdateWord(resultFields, wordRequest);
        }
    }

    @Nested
    @DisplayName(WORD_REMOVE_CONTROLLER_TEST)
    class RemoveWord {

        @Test
        @DisplayName(SUCCESS)
        void removeWordSuccess() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(REMOVE_WORD_SUCCESS.getMessage())
                    .build();
            // when
            doNothing().when(wordService).remove(anyLong(), anyLong());

            // then
            testHelper.assertRemoveWord(resultFields);
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void removeWordFailMemberNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(MemberNotFoundException.class).when(wordService).remove(anyLong(), anyLong());

            // then
            testHelper.assertRemoveWord(resultFields);
        }

        @Test
        @DisplayName(FAIL_WORD_NOT_FOUND)
        void removeWordFailWordNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isBadRequest())
                    .status(400)
                    .result(false)
                    .message(WORD_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(WordNotFoundException.class).when(wordService).remove(anyLong(), anyLong());

            // then
            testHelper.assertRemoveWord(resultFields);
        }
    }
}