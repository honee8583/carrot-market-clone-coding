package com.carrot.carrotmarketclonecoding.member.helper;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.carrot.carrotmarketclonecoding.util.ControllerTestUtil;
import com.carrot.carrotmarketclonecoding.util.ResultFields;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultHandler;

public class MemberTestHelper extends ControllerTestUtil {

    private final MemberRequestHelper requestHelper;
    private final MemberRestDocsHelper restDocsHelper;

    public MemberTestHelper(MockMvc mvc, RestDocumentationResultHandler restDocs) {
        this.requestHelper = new MemberRequestHelper(mvc);
        this.restDocsHelper = new MemberRestDocsHelper(restDocs);
    }

    public void assertUpdateProfileSuccess(ResultFields resultFields,
                                           MockMultipartFile profileImage,
                                           MockMultipartFile profileUpdateRequest) throws Exception {
        assertUpdateProfile(resultFields, profileImage, profileUpdateRequest, restDocsHelper.createUpdateProfileSuccessDocument());
    }

    public void assertUpdateProfileFail(ResultFields resultFields,
                                           MockMultipartFile profileImage,
                                           MockMultipartFile profileUpdateRequest) throws Exception {
        assertUpdateProfile(resultFields, profileImage, profileUpdateRequest, restDocsHelper.createResponseResultDocument());
    }

    private void assertUpdateProfile(ResultFields resultFields,
                                        MockMultipartFile profileImage,
                                        MockMultipartFile profileUpdateRequest,
                                    ResultHandler document) throws Exception {
        assertResponseResult(requestHelper.requestUpdateProfile(profileImage, profileUpdateRequest), resultFields)
                .andDo(document);
    }

    public void assertGetProfileDetailSuccess(ResultFields resultFields) throws Exception {
        assertResponseResult(requestHelper.requestGetProfileDetail(), resultFields)
                .andExpect(jsonPath("$.data.nickname", equalTo("nickname")))
                .andExpect(jsonPath("$.data.profileUrl", equalTo("profileUrl")))
                .andDo(restDocsHelper.createGetProfileDetailSuccessDocument());
    }

    public void assertGetProfileDetailFail(ResultFields resultFields) throws Exception {
        assertResponseResult(requestHelper.requestGetProfileDetail(), resultFields)
                .andExpect(jsonPath("$.data", equalTo(null)))
                .andDo(restDocsHelper.createResponseResultDocument());
    }
}
