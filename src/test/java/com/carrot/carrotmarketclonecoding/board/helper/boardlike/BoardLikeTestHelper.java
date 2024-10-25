package com.carrot.carrotmarketclonecoding.board.helper.boardlike;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.carrot.carrotmarketclonecoding.util.ControllerTestUtil;
import com.carrot.carrotmarketclonecoding.util.ResultFields;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;

@Import(value = {
        BoardLikeRequestHelper.class,
        BoardLikeRestDocsHelper.class
})
@TestComponent
public class BoardLikeTestHelper extends ControllerTestUtil {

    private final BoardLikeRequestHelper requestHelper;
    private final BoardLikeRestDocsHelper docsHelper;

    public BoardLikeTestHelper(MockMvc mvc, RestDocumentationResultHandler restDocs) {
        this.requestHelper = new BoardLikeRequestHelper(mvc);
        this.docsHelper = new BoardLikeRestDocsHelper(restDocs);
    }

    public void assertLikeBoard(ResultFields resultFields)
            throws Exception {
        assertResponseResult(requestHelper.requestLikeBoard(), resultFields)
                .andExpect(jsonPath("$.data", equalTo(null)))
                .andDo(docsHelper.createAddBoardLikeDocument());
    }

    public void assertGetUserLikedBoardsSuccess(ResultFields resultFields)
            throws Exception {
        assertResponseResult(requestHelper.requestGetUserLikedBoards(), resultFields)
                .andExpect(jsonPath("$.data.contents.size()", equalTo(2)))
                .andExpect(jsonPath("$.data.totalPage", equalTo(1)))
                .andExpect(jsonPath("$.data.totalElements", equalTo(2)))
                .andExpect(jsonPath("$.data.first", equalTo(true)))
                .andExpect(jsonPath("$.data.last", equalTo(true)))
                .andExpect(jsonPath("$.data.numberOfElements", equalTo(2)))
                .andDo(docsHelper.createGetUserLikedBoardsSuccessDocument());
    }

    public void assertGetUserLikedBoardsFailed(ResultFields resultFields)
            throws Exception{
        assertResponseResult(requestHelper.requestGetUserLikedBoards(), resultFields)
                .andExpect(jsonPath("$.data", equalTo(null)))
                .andDo(docsHelper.createGetUserLikedBoardsFailedDocument());
    }
}
