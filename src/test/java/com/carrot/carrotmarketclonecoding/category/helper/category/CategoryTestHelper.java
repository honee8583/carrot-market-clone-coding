package com.carrot.carrotmarketclonecoding.category.helper.category;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.carrot.carrotmarketclonecoding.util.ControllerTestUtil;
import com.carrot.carrotmarketclonecoding.util.ResultFields;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;

public class CategoryTestHelper extends ControllerTestUtil {
    private final CategoryRequestHelper requestHelper;
    private final CategoryRestDocsHelper restDocsHelper;

    public CategoryTestHelper(MockMvc mvc, RestDocumentationResultHandler restDocs) {
        this.requestHelper = new CategoryRequestHelper(mvc);
        this.restDocsHelper = new CategoryRestDocsHelper(restDocs);
    }

    public void assertGetAllCategories(ResultFields resultFields) throws Exception {
        assertResponseResult(requestHelper.requestGetAllCategories(), resultFields)
                .andExpect(jsonPath("$.data.size()", equalTo(3)))
                .andDo(restDocsHelper.createGetAllCategoriesDocument());
    }
}
