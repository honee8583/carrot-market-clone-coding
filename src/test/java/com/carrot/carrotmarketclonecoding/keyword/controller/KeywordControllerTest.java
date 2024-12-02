package com.carrot.carrotmarketclonecoding.keyword.controller;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.*;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrot.carrotmarketclonecoding.ControllerTest;
import com.carrot.carrotmarketclonecoding.common.exception.CategoryNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.KeywordNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.KeywordOverLimitException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.UnauthorizedAccessException;
import com.carrot.carrotmarketclonecoding.keyword.dto.KeywordRequestDto.KeywordCreateRequestDto;
import com.carrot.carrotmarketclonecoding.keyword.dto.KeywordRequestDto.KeywordEditRequestDto;
import com.carrot.carrotmarketclonecoding.keyword.dto.KeywordResponseDto.KeywordDetailResponseDto;
import com.carrot.carrotmarketclonecoding.keyword.helper.KeywordDtoFactory;
import com.carrot.carrotmarketclonecoding.keyword.helper.KeywordTestHelper;
import com.carrot.carrotmarketclonecoding.util.ResultFields;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class KeywordControllerTest extends ControllerTest {

    private KeywordTestHelper testHelper;

    @Autowired
    private KeywordDtoFactory dtoFactory;

    @BeforeEach
    void setUp() {
        this.testHelper = new KeywordTestHelper(mvc, restDocs);
    }

    @Nested
    @DisplayName("키워드 추가 컨트롤러 테스트")
    class AddKeyword {

        private final KeywordCreateRequestDto requestDto = dtoFactory.createKeywordRequestDto();

        @Test
        @DisplayName("성공")
        void addSuccess() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isCreated())
                    .status(201)
                    .result(true)
                    .message(ADD_KEYWORD_SUCCESS.getMessage())
                    .build();

            // when
            doNothing().when(keywordService).add(anyLong(), any(KeywordCreateRequestDto.class));


            // then
            testHelper.assertAddKeyword(resultFields, requestDto);
        }

        @Test
        @DisplayName("존재하지 않는 사용자일 경우 401 반환")
        void addFailMemberNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(MemberNotFoundException.class).when(keywordService).add(anyLong(), any(KeywordCreateRequestDto.class));

            // then
            testHelper.assertAddKeyword(resultFields, requestDto);
        }

        @Test
        @DisplayName("사용자의 키워드 개수가 30개를 넘어갈경우 400 반환")
        void addFailKeywordOverLimit() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isBadRequest())
                    .status(400)
                    .result(false)
                    .message(KEYWORD_OVER_LIMIT.getMessage())
                    .build();

            // when
            doThrow(KeywordOverLimitException.class).when(keywordService).add(anyLong(), any(KeywordCreateRequestDto.class));

            // then
            testHelper.assertAddKeyword(resultFields, requestDto);
        }
    }

    @Nested
    @DisplayName("사용자의 키워드 목록 조회 컨트롤러 테스트")
    class GetKeywords {

        @Test
        @DisplayName("성공")
        void getKeywordsSuccess() throws Exception {
            // given
            List<KeywordDetailResponseDto> keywords = dtoFactory.createKeywordDetailResponseDto();

            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(GET_KEYWORDS_SUCCESS.getMessage())
                    .build();

            // when
            when(keywordService.getAllKeywords(anyLong())).thenReturn(keywords);

            // then
            testHelper.assertGetKeywordsSuccess(resultFields);
        }

        @Test
        @DisplayName("존재하지 않는 사용자일 경우 401 반환")
        void getKeywordsFailMemberNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(MemberNotFoundException.class).when(keywordService).getAllKeywords(anyLong());

            // then
            testHelper.assertGetKeywordsFail(resultFields);
        }
    }

    @Nested
    @DisplayName("키워드 편집 컨트롤러 테스트")
    class EditKeyword {

        private final KeywordEditRequestDto editRequestDto = dtoFactory.createKeywordEditRequestDto();

        @Test
        @DisplayName("성공")
        void editSuccess() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(EDIT_KEYWORD_SUCCESS.getMessage())
                    .build();

            // when
            doNothing().when(keywordService).edit(anyLong(), anyLong(), any(KeywordEditRequestDto.class));

            // then
            testHelper.assertEditKeyword(resultFields, editRequestDto);
        }

        @Test
        @DisplayName("사용자가 존재하지 않을 경우 401 반환")
        void editFailMemberNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(MemberNotFoundException.class).when(keywordService)
                    .edit(anyLong(), anyLong(), any(KeywordEditRequestDto.class));

            // then
            testHelper.assertEditKeyword(resultFields, editRequestDto);
        }

        @Test
        @DisplayName("편집할 키워드가 존재하지 않을 경우 400 반환")
        void editFailKeywordNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isBadRequest())
                    .status(400)
                    .result(false)
                    .message(KEYWORD_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(KeywordNotFoundException.class).when(keywordService)
                    .edit(anyLong(), anyLong(), any(KeywordEditRequestDto.class));

            // then
            testHelper.assertEditKeyword(resultFields, editRequestDto);
        }

        @Test
        @DisplayName("편집할 키워드가 사용자의 키워드가 아닐 경우 400 반환")
        void editFailNotKeywordMember() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isForbidden())
                    .status(403)
                    .result(false)
                    .message(UNAUTHORIZED_ACCESS.getMessage())
                    .build();

            // when
            doThrow(UnauthorizedAccessException.class).when(keywordService)
                    .edit(anyLong(), anyLong(), any(KeywordEditRequestDto.class));

            // then
            testHelper.assertEditKeyword(resultFields, editRequestDto);
        }

        @Test
        @DisplayName("저장할 카테고리가 존재하지 않을 경우 400 반환")
        void editFailCategoryNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isBadRequest())
                    .status(400)
                    .result(false)
                    .message(CATEGORY_NOT_FOUND.getMessage())
                    .build();


            // when
            doThrow(CategoryNotFoundException.class).when(keywordService)
                    .edit(anyLong(), anyLong(), any(KeywordEditRequestDto.class));

            // then
            testHelper.assertEditKeyword(resultFields, editRequestDto);
        }
    }

    @Nested
    @DisplayName("키워드 삭제 컨트롤러 테스트")
    class DeleteKeyword {

        @Test
        @DisplayName("성공")
        void deleteSuccess() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(DELETE_KEYWORDS_SUCCESS.getMessage())
                    .build();

            // when
            doNothing().when(keywordService).delete(anyLong(), anyLong());

            // then
            testHelper.assertDeleteKeyword(resultFields);
        }

        @Test
        @DisplayName("사용자가 존재하지 않을 경우 401 반환")
        void deleteFailMemberNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(MemberNotFoundException.class).when(keywordService).delete(anyLong(), anyLong());

            // then
            testHelper.assertDeleteKeyword(resultFields);
        }

        @Test
        @DisplayName("삭제할 키워드가 존재하지 않을 경우 400 반환")
        void deleteFailKeywordNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isBadRequest())
                    .status(400)
                    .result(false)
                    .message(KEYWORD_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(KeywordNotFoundException.class).when(keywordService).delete(anyLong(), anyLong());

            // then
            testHelper.assertDeleteKeyword(resultFields);
        }
    }
}