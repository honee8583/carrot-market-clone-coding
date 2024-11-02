package com.carrot.carrotmarketclonecoding.keyword.helper;

import com.carrot.carrotmarketclonecoding.keyword.dto.KeywordRequestDto.KeywordCreateRequestDto;
import com.carrot.carrotmarketclonecoding.keyword.dto.KeywordRequestDto.KeywordEditRequestDto;
import com.carrot.carrotmarketclonecoding.util.ControllerTestUtil;
import com.carrot.carrotmarketclonecoding.util.ResultFields;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultHandler;

public class KeywordTestHelper extends ControllerTestUtil {

    private final KeywordRequestHelper requestHelper;
    private final KeywordRestDocsHelper restDocsHelper;

    public KeywordTestHelper(MockMvc mvc, RestDocumentationResultHandler restDocs) {
        this.requestHelper = new KeywordRequestHelper(mvc);
        this.restDocsHelper = new KeywordRestDocsHelper(restDocs);
    }

    public void assertAddKeyword(ResultFields resultFields, KeywordCreateRequestDto requestDto) throws Exception {
        assertResponseResult(requestHelper.requestAddKeyword(requestDto), resultFields)
                .andDo(restDocsHelper.createAddKeywordDocument());
    }

    public void assertGetKeywordsSuccess(ResultFields resultFields) throws Exception {
        assertGetKeywords(resultFields, restDocsHelper.createGetKeywordsSuccessDocument());
    }

    public void assertGetKeywordsFail(ResultFields resultFields) throws Exception {
        assertGetKeywords(resultFields, restDocsHelper.createCommonDocument());
    }

    private void assertGetKeywords (ResultFields resultFields, ResultHandler document) throws Exception {
        assertResponseResult(requestHelper.requestGetKeywords(), resultFields)
                .andDo(document);
    }

    public void assertEditKeyword(ResultFields resultFields, KeywordEditRequestDto editRequestDto) throws Exception {
        assertResponseResult(requestHelper.requestEditKeyword(editRequestDto), resultFields)
                .andDo(restDocsHelper.createEditKeywordDocument());
    }

    public void assertDeleteKeyword(ResultFields resultFields) throws Exception {
        assertResponseResult(requestHelper.requestDeleteKeyword(), resultFields)
                .andDo(restDocsHelper.createDeleteKeywordDocument());
    }
}
