package com.carrot.carrotmarketclonecoding.board.helper.searchkeyword;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;

import com.carrot.carrotmarketclonecoding.util.ControllerRequestUtil;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@TestComponent
public class SearchKeywordRequestHelper extends ControllerRequestUtil {

    private final MockMvc mvc;

    private static final String KEYWORD_TOP_RANK_URL = "/search/rank";
    private static final String RECENT_SEARCH_KEYWORD_URL = "/search/recent";
    private static final String REMOVE_ALL_RECENT_SEARCH_KEYWORDS_URL = "/search/recent/all";

    public SearchKeywordRequestHelper(MockMvc mvc) {
        this.mvc = mvc;
    }

    public ResultActions requestGetKeywordTopRank() throws Exception {
        return mvc.perform(get(KEYWORD_TOP_RANK_URL));
    }

    public ResultActions requestGetRecentSearchKeyword() throws Exception {
        return mvc.perform(get(RECENT_SEARCH_KEYWORD_URL));
    }

    public ResultActions requestRemoveRecentSearchKeyword() throws Exception {
        return mvc.perform(
                requestWithCsrf(delete(RECENT_SEARCH_KEYWORD_URL + "?keyword=keyword"))
        );
    }

    public ResultActions requestRemoveAllRecentSearchKeywords() throws Exception {
        return mvc.perform(
                requestWithCsrf(delete(REMOVE_ALL_RECENT_SEARCH_KEYWORDS_URL))
        );
    }
}
