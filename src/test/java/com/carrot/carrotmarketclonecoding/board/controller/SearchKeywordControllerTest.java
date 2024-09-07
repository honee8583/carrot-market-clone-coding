package com.carrot.carrotmarketclonecoding.board.controller;

import static com.carrot.carrotmarketclonecoding.board.SearchKeywordTestDisplayNames.MESSAGE.*;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.MEMBER_NOT_FOUND;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.*;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrot.carrotmarketclonecoding.board.service.impl.SearchKeywordServiceImpl;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = SearchKeywordController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
class SearchKeywordControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SearchKeywordServiceImpl searchKeywordService;

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

            // when
            when(searchKeywordService.getTopSearchKeywords()).thenReturn(keywords);

            // then
            mvc.perform(get("/search/rank"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", equalTo(200)))
                    .andExpect(jsonPath("$.result", equalTo(true)))
                    .andExpect(jsonPath("$.message", equalTo(GET_TOP_RANK_SEARCH_KEYWORDS_SUCCESS.getMessage())))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0]", equalTo("keyword1")))
                    .andExpect(jsonPath("$.data[1]", equalTo("keyword2")));
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

            // when
            when(searchKeywordService.getRecentSearchKeywords(anyLong())).thenReturn(keywords);

            // then
            mvc.perform(get("/search/recent"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", equalTo(200)))
                    .andExpect(jsonPath("$.result", equalTo(true)))
                    .andExpect(jsonPath("$.message", equalTo(GET_RECENT_SEARCH_KEYWORDS_SUCCESS.getMessage())))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0]", equalTo("keyword1")))
                    .andExpect(jsonPath("$.data[1]", equalTo("keyword2")));
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void recentSearchKeywordFailMemberNotFound() throws Exception {
            // given

            // when
            doThrow(MemberNotFoundException.class).when(searchKeywordService).getRecentSearchKeywords(anyLong());

            // then
            mvc.perform(get("/search/recent"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status", equalTo(401)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(MEMBER_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }
    }

    @Nested
    @DisplayName(REMOVE_RECENT_SEARCH_KEYWORD_CONTROLLER_TEST)
    class RemoveRecentSearchKeyword {

        @Test
        @DisplayName(SUCCESS)
        void removeRecentSearchKeywordSuccess() throws Exception {
            // given
            // when
            doNothing().when(searchKeywordService).removeRecentSearchKeyword(anyLong(), anyString());

            // then
            mvc.perform(delete("/search/recent")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("keyword", "keyword"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", equalTo(200)))
                    .andExpect(jsonPath("$.result", equalTo(true)))
                    .andExpect(jsonPath("$.message", equalTo(REMOVE_RECENT_SEARCH_KEYWORD_SUCCESS.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void removeRecentSearchKeywordFailMemberNotFound() throws Exception {
            // given

            // when
            doThrow(MemberNotFoundException.class).when(searchKeywordService).removeRecentSearchKeyword(anyLong(), any());

            // then
            mvc.perform(delete("/search/recent")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("keyword", "keyword"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status", equalTo(401)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(MEMBER_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }
    }

    @Nested
    @DisplayName(REMOVE_ALL_RECENT_SEARCH_KEYWORDS_CONTROLLER_TEST)
    class RemoveAllRecentSearchKeywords {

        @Test
        @DisplayName(SUCCESS)
        void removeAllRecentSearchKeywordsSuccess() throws Exception {
            // given
            // when
            doNothing().when(searchKeywordService).removeAllRecentSearchKeywords(anyLong());

            // then
            mvc.perform(delete("/search/recent/all"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", equalTo(200)))
                    .andExpect(jsonPath("$.result", equalTo(true)))
                    .andExpect(jsonPath("$.message", equalTo(REMOVE_ALL_RECENT_SEARCH_KEYWORD_SUCCESS.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void removeAllRecentSearchKeywordsFailMemberNotFound() throws Exception {
            // given
            // when
            doThrow(MemberNotFoundException.class).when(searchKeywordService).removeAllRecentSearchKeywords(anyLong());

            // then
            mvc.perform(delete("/search/recent/all"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status", equalTo(401)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(MEMBER_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }
    }
}