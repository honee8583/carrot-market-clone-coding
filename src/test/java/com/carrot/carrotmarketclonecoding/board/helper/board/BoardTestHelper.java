package com.carrot.carrotmarketclonecoding.board.helper.board;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.carrot.carrotmarketclonecoding.util.ControllerTestUtil;
import com.carrot.carrotmarketclonecoding.util.ResultFields;
import java.util.Map;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@Import(value = {
        BoardRequestHelper.class,
        BoardRestDocsHelper.class
})
@TestComponent
public class BoardTestHelper extends ControllerTestUtil {

    private final BoardRequestHelper requestHelper;
    private final BoardRestDocsHelper docsHelper;

    public BoardTestHelper(MockMvc mvc, RestDocumentationResultHandler restDocs) {
        this.requestHelper = new BoardRequestHelper(mvc);
        this.docsHelper = new BoardRestDocsHelper(restDocs);
    }

    public void assertRegisterBoardSuccess(ResultFields resultFields,
                                           MockMultipartFile request,
                                           MockMultipartFile[] pictures) throws Exception {
        assertRegisterBoard(resultFields, request, pictures, docsHelper.createRegisterBoardSuccessDocument());
    }

    public void assertRegisterBoardFailed(ResultFields resultFields,
                                          MockMultipartFile request,
                                           MockMultipartFile[] pictures) throws Exception {
        assertRegisterBoard(resultFields, request, pictures, docsHelper.createCommonResponseDocument());
    }

    private void assertRegisterBoard(ResultFields resultFields,
                                     MockMultipartFile request,
                                     MockMultipartFile[] pictures,
                                     RestDocumentationResultHandler restDocs) throws Exception {
        assertResponseResult(
                requestHelper.requestRegisterBoard(request, pictures), resultFields)
                .andExpect(jsonPath("$.data", equalTo(null)))
                .andDo(restDocs);
    }

    public void assertRegisterTmpBoardSuccess(ResultFields resultFields,
                                              MockMultipartFile request,
                                              MockMultipartFile[] pictures) throws Exception {
        assertResponseResult(requestHelper.requestRegisterTmpBoard(request, pictures), resultFields)
                .andExpect(jsonPath("$.data", equalTo(null)))
                .andDo(docsHelper.createRegisterBoardSuccessDocument());
    }

    public void assertRegisterBoardFailedInvalidInput(ResultFields resultFields,
                                                      MockMultipartFile request,
                                                      MockMultipartFile[] pictures,
                                                      Map<String, String> resultMap) throws Exception {
        assertResponseResult(requestHelper.requestRegisterBoard(request, pictures), resultFields)
                .andExpect(jsonPath("$.data", equalTo(resultMap)))
                .andDo(docsHelper.createRegisterBoardFailedInvalidInputDocument());
    }

    public void assertGetBoardDetailSuccess(ResultFields resultFields, String dataJsonPath, Object data) throws Exception {
        assertResponseResult(requestHelper.requestGetBoardDetail(), resultFields)
                .andExpect(jsonPath(dataJsonPath, equalTo(data)))
                .andDo(docsHelper.createGetBoardDetailSuccessDocument());
    }

    public void assertGetBoardDetailFailed(ResultFields resultFields) throws Exception {
        assertResponseResult(requestHelper.requestGetBoardDetail(), resultFields)
                .andExpect(jsonPath("$.data", equalTo(null)))
                .andDo(docsHelper.createGetBoardDetailFailedDocument());
    }

    public void assertSearchBoardsSuccess(ResultFields resultFields, int contentSize) throws Exception {
        assertSearchBoards(resultFields)
                .andExpect(jsonPath("$.data.contents.size()", equalTo(contentSize)))
                .andDo(docsHelper.createSearchBoardsSuccessDocument());
    }

    public void assertSearchBoardsFailed(ResultFields resultFields) throws Exception {
        assertSearchBoards(resultFields)
                .andExpect(jsonPath("$.data", equalTo(null)))
                .andDo(docsHelper.createSearchBoardsFailedDocument());
    }

    private ResultActions assertSearchBoards(ResultFields resultFields) throws Exception {
        return assertResponseResult(requestHelper.requestGetSearchBoards(), resultFields);
    }

    public void assertSearchMyBoardsSuccess(ResultFields resultFields) throws Exception {
        assertSearchMyBoards(resultFields)
                .andExpect(jsonPath("$.data.contents.size()", equalTo(30)))
                .andExpect(jsonPath("$.data.totalPage", equalTo(3)))
                .andExpect(jsonPath("$.data.totalElements", equalTo(30)))
                .andExpect(jsonPath("$.data.first", equalTo(true)))
                .andExpect(jsonPath("$.data.last", equalTo(false)))
                .andExpect(jsonPath("$.data.numberOfElements", equalTo(30)))
                .andDo(docsHelper.createSearchMyBoardsSuccessDocument());
    }

    public void assertSearchMyBoardsFailed(ResultFields resultFields) throws Exception {
        assertSearchMyBoards(resultFields)
                .andExpect(jsonPath("$.data", equalTo(null)))
                .andDo(docsHelper.createCommonResponseDocument());
    }

    private ResultActions assertSearchMyBoards(ResultFields resultFields) throws Exception {
        return assertResponseResult(requestHelper.requestSearchMyBoards(), resultFields);
    }

    public void assertUpdateBoard(ResultFields resultFields, MockMultipartFile[] newPictures, MockMultipartFile updateRequest) throws Exception {
        assertResponseResult(requestHelper.requestUpdateBoard(newPictures, updateRequest), resultFields)
                .andExpect(jsonPath("$.data", equalTo(null)))
                .andDo(docsHelper.createUpdateBoardDocument());
    }

    public void assertDeleteBoard(ResultFields resultFields) throws Exception {
        assertResponseResult(requestHelper.requestDeleteBoard(), resultFields)
                .andExpect(jsonPath("$.data", equalTo(null)))
                .andDo(docsHelper.createDeleteBoardSuccessDocument());
    }

    public void assertGetTmpBoardDetailSuccess(ResultFields resultFields, String jsonPath, Object data) throws Exception {
        assertResponseResult(requestHelper.requestGetTmpBoard(), resultFields)
                .andExpect(jsonPath(jsonPath, equalTo(data)))
                .andDo(docsHelper.createGetTmpBoardSuccessDocument());
    }

    public void assertTmpBoardDetailFailed(ResultFields resultFields) throws Exception {
        assertResponseResult(requestHelper.requestGetTmpBoard(), resultFields)
                .andExpect(jsonPath("$.data", equalTo(null)))
                .andDo(docsHelper.createCommonResponseDocument());
    }
}
