package com.carrot.carrotmarketclonecoding.board.controller;

import static com.carrot.carrotmarketclonecoding.board.displayname.SearchKeywordTestDisplayNames.MESSAGE.*;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.MEMBER_NOT_FOUND;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrot.carrotmarketclonecoding.auth.config.WithCustomMockUser;
import com.carrot.carrotmarketclonecoding.board.helper.searchkeyword.SearchKeywordTestHelper;
import com.carrot.carrotmarketclonecoding.board.service.SearchKeywordService;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.util.RestDocsTestUtil;
import com.carrot.carrotmarketclonecoding.util.ResultFields;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WithCustomMockUser
@WebMvcTest(controllers = SearchKeywordController.class)
class SearchKeywordControllerTest extends RestDocsTestUtil {

    private SearchKeywordTestHelper testHelper;

    @MockBean
    private SearchKeywordService searchKeywordService;

    @BeforeEach
    public void setUp() {
        this.testHelper = new SearchKeywordTestHelper(mvc, restDocs);
    }

    @Nested
    @DisplayName(SEARCH_KEYWORD_TOP_RANK_CONTROLLER_TEST)
    class SearchKeywordTopRank {

        @Test
        @DisplayName(SUCCESS)
        void searchKeywordTopRankSuccess() throws Exception {
            // given
            Set<String> keywords = new HashSet<>();
            keywords.add("keyword1");
            keywords.add("keyword2");
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(GET_TOP_RANK_SEARCH_KEYWORDS_SUCCESS.getMessage())
                    .build();

            // when
            when(searchKeywordService.getTopSearchKeywords()).thenReturn(keywords);

            // then
            testHelper.assertSearchKeywordTopRankSuccess(resultFields);
        }
    }

    @Nested
    @DisplayName(RECENT_SEARCH_KEYWORD_CONTROLLER_TEST)
    class RecentSearchKeywords {

        @Test
        @DisplayName(SUCCESS)
        void recentSearchKeywordSuccess() throws Exception {
            // given
            List<String> keywords = Arrays.asList("keyword1", "keyword2");
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(GET_RECENT_SEARCH_KEYWORDS_SUCCESS.getMessage())
                    .build();

            // when
            when(searchKeywordService.getRecentSearchKeywords(anyLong())).thenReturn(keywords);

            // then
            testHelper.assertRecentSearchKeywordSuccess(resultFields);
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void recentSearchKeywordFailMemberNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(MemberNotFoundException.class).when(searchKeywordService).getRecentSearchKeywords(anyLong());

            // then
            testHelper.assertRecentSearchKeywordFail(resultFields);
        }
    }

    @Nested
    @DisplayName(REMOVE_RECENT_SEARCH_KEYWORD_CONTROLLER_TEST)
    class RemoveRecentSearchKeyword {

        @Test
        @DisplayName(SUCCESS)
        void removeRecentSearchKeywordSuccess() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(REMOVE_RECENT_SEARCH_KEYWORD_SUCCESS.getMessage())
                    .build();

            // when
            doNothing().when(searchKeywordService).removeRecentSearchKeyword(anyLong(), anyString());

            // then
            testHelper.assertRemoveRecentSearchKeyword(resultFields);
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void removeRecentSearchKeywordFailMemberNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(MemberNotFoundException.class).when(searchKeywordService).removeRecentSearchKeyword(anyLong(), any());

            // then
            testHelper.assertRemoveRecentSearchKeyword(resultFields);
        }
    }

    @Nested
    @DisplayName(REMOVE_ALL_RECENT_SEARCH_KEYWORDS_CONTROLLER_TEST)
    class RemoveAllRecentSearchKeywords {

        @Test
        @DisplayName(SUCCESS)
        void removeAllRecentSearchKeywordsSuccess() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(REMOVE_ALL_RECENT_SEARCH_KEYWORD_SUCCESS.getMessage())
                    .build();

            // when
            doNothing().when(searchKeywordService).removeAllRecentSearchKeywords(anyLong());

            // then
            testHelper.assertRemoveAllRecentSearchKeywords(resultFields);
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void removeAllRecentSearchKeywordsFailMemberNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(MemberNotFoundException.class).when(searchKeywordService).removeAllRecentSearchKeywords(anyLong());

            // then
            testHelper.assertRemoveAllRecentSearchKeywords(resultFields);
        }
    }
}