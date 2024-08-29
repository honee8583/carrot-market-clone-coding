package com.carrot.carrotmarketclonecoding.board.controller;

import static com.carrot.carrotmarketclonecoding.board.SearchKeywordTestDisplayNames.MESSAGE.*;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.*;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrot.carrotmarketclonecoding.board.service.SearchKeywordService;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SearchKeywordController.class)
class SearchKeywordControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SearchKeywordService searchKeywordService;

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
            when(searchKeywordService.getTopSearchRank()).thenReturn(keywords);

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
            when(searchKeywordService.getRecentSearches(anyLong())).thenReturn(keywords);

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
    }
}