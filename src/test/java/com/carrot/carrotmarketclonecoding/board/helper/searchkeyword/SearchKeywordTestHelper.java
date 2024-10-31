package com.carrot.carrotmarketclonecoding.board.helper.searchkeyword;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.carrot.carrotmarketclonecoding.util.ControllerTestUtil;
import com.carrot.carrotmarketclonecoding.util.ResultFields;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@Import(value = {SearchKeywordRequestHelper.class, SearchKeywordRestDocsHelper.class})
@TestComponent
public class SearchKeywordTestHelper extends ControllerTestUtil {

    private final SearchKeywordRequestHelper requestHelper;
    private final SearchKeywordRestDocsHelper docsHelper;

    public SearchKeywordTestHelper(MockMvc mvc, RestDocumentationResultHandler restDocs) {
        this.requestHelper = new SearchKeywordRequestHelper(mvc);
        this.docsHelper = new SearchKeywordRestDocsHelper(restDocs);
    }

    public void assertSearchKeywordTopRankSuccess(ResultFields resultFields) throws Exception {
        assertSearchKeyword(requestHelper.requestGetKeywordTopRank(), resultFields);
    }

    public void assertRecentSearchKeywordSuccess(ResultFields resultFields) throws Exception {
        assertSearchKeyword(requestHelper.requestGetRecentSearchKeyword(), resultFields);
    }

    private void assertSearchKeyword(ResultActions resultActions, ResultFields resultFields) throws Exception {
        assertResponseResult(resultActions, resultFields)
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0]", equalTo("keyword1")))
                .andExpect(jsonPath("$.data[1]", equalTo("keyword2")))
                .andDo(docsHelper.createSearchKeywordSuccessDocument());
    }

    public void assertRecentSearchKeywordFail(ResultFields resultFields) throws Exception {
        assertResponseResult(requestHelper.requestGetRecentSearchKeyword(), resultFields)
                .andDo(docsHelper.createResponseResultDocument());
    }

    public void assertRemoveRecentSearchKeyword(ResultFields resultFields) throws Exception {
        assertResponseResult(requestHelper.requestRemoveRecentSearchKeyword(), resultFields)
                .andExpect(jsonPath("$.data", equalTo(null)))
                .andDo(docsHelper.createRemoveRecentSearchKeywordDocument());
    }

    public void assertRemoveAllRecentSearchKeywords(ResultFields resultFields) throws Exception {
        assertResponseResult(requestHelper.requestRemoveAllRecentSearchKeywords(), resultFields)
                .andExpect(jsonPath("$.data", equalTo(null)))
                .andDo(docsHelper.createResponseResultDocument());
    }
}
