package com.carrot.carrotmarketclonecoding.word.helper;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.carrot.carrotmarketclonecoding.util.ControllerTestUtil;
import com.carrot.carrotmarketclonecoding.util.ResultFields;
import com.carrot.carrotmarketclonecoding.word.dto.WordRequestDto;
import java.util.Map;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

public class WordTestHelper extends ControllerTestUtil {

    private final WordRequestHelper requestHelper;
    private final WordRestDocsHelper restDocsHelper;

    public WordTestHelper(MockMvc mvc, RestDocumentationResultHandler restDocs) {
        this.requestHelper = new WordRequestHelper(mvc);
        this.restDocsHelper = new WordRestDocsHelper(restDocs);
    }

    public void assertAddWord(ResultFields resultFields, WordRequestDto wordRequest) throws Exception {
        assertResponseResult(requestHelper.requestAddWord(wordRequest), resultFields)
                .andExpect(jsonPath("$.data", equalTo(null)))
                .andDo(restDocsHelper.createAddWordSuccessDocument());
    }

    public void assertAddWordInputNotValid(ResultFields resultFields, WordRequestDto wordRequest, Map<String, String> map) throws Exception {
        assertResponseResult(requestHelper.requestAddWord(wordRequest), resultFields)
                .andExpect(jsonPath("$.data", equalTo(map)))
                .andDo(restDocsHelper.createInputNotValidDocument());
    }

    public void assertGetWordsSuccess(ResultFields resultFields) throws Exception {
        assertGetWords(resultFields).andExpect(jsonPath("$.data.size()", equalTo(2)))
                .andDo(restDocsHelper.createGetWordsSuccessDocument());
    }

    public void assertGetWordsFail(ResultFields resultFields) throws Exception {
        assertGetWords(resultFields).andExpect(jsonPath("$.data", equalTo(null)))
                .andDo(restDocsHelper.createResponseResultDocument());
    }

    private ResultActions assertGetWords(ResultFields resultFields) throws Exception {
        return assertResponseResult(requestHelper.requestGetWords(), resultFields);
    }

    public void assertUpdateWord(ResultFields resultFields, WordRequestDto wordRequest) throws Exception {
        assertResponseResult(requestHelper.requestUpdateWord(wordRequest), resultFields)
                .andExpect(jsonPath("$.data", equalTo(null)))
                .andDo(restDocsHelper.createUpdateAndRemoveWordDocument());
    }

    public void assertRemoveWord(ResultFields resultFields) throws Exception {
        assertResponseResult(requestHelper.requestRemoveWord(), resultFields)
                .andExpect(jsonPath("$.data", equalTo(null)))
                .andDo(restDocsHelper.createUpdateAndRemoveWordDocument());
    }
}
