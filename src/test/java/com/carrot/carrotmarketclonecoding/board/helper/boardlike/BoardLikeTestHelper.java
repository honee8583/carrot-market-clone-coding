package com.carrot.carrotmarketclonecoding.board.helper.boardlike;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.carrot.carrotmarketclonecoding.util.ResultFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@TestComponent
public class BoardLikeTestHelper {

    @Autowired
    private BoardLikeRequestHelper requestHelper;

    @Autowired
    private BoardLikeRestDocsHelper docsHelper;

    public void assertLikeBoard(MockMvc mvc, ResultFields resultFields, RestDocumentationResultHandler restDocs)
            throws Exception {
        assertResponseResult(requestHelper.requestLikeBoard(mvc), resultFields)
                .andExpect(jsonPath("$.data", equalTo(null)))
                .andDo(docsHelper.createAddBoardLikeDocument(restDocs));
    }

    public void assertGetUserLikedBoardsSuccess(MockMvc mvc,
                                                ResultFields resultFields,
                                                RestDocumentationResultHandler restDocs)
            throws Exception {
        assertResponseResult(requestHelper.requestGetUserLikedBoards(mvc), resultFields)
                .andExpect(jsonPath("$.data.contents.size()", equalTo(2)))
                .andExpect(jsonPath("$.data.totalPage", equalTo(1)))
                .andExpect(jsonPath("$.data.totalElements", equalTo(2)))
                .andExpect(jsonPath("$.data.first", equalTo(true)))
                .andExpect(jsonPath("$.data.last", equalTo(true)))
                .andExpect(jsonPath("$.data.numberOfElements", equalTo(2)))
                .andDo(docsHelper.createGetUserLikedBoardsSuccessDocument(restDocs));
    }

    public void assertGetUserLikedBoardsFailed(MockMvc mvc,
                                             ResultFields resultFields,
                                             RestDocumentationResultHandler restDocs)
            throws Exception{
        assertResponseResult(requestHelper.requestGetUserLikedBoards(mvc), resultFields)
                .andExpect(jsonPath("$.data", equalTo(null)))
                .andDo(docsHelper.createGetUserLikedBoardsFailedDocument(restDocs));
    }

    // TODO 분리
    private ResultActions assertResponseResult(ResultActions resultActions, ResultFields resultFields) throws Exception {
        return resultActions
                .andExpect(resultFields.getResultMatcher())
                .andExpect(jsonPath("$.status", equalTo(resultFields.getStatus())))
                .andExpect(jsonPath("$.result", equalTo(resultFields.isResult())))
                .andExpect(jsonPath("$.message", equalTo(resultFields.getMessage())));
    }
}
