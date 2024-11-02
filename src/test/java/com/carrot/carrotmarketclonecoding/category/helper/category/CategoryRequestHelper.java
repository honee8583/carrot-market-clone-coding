package com.carrot.carrotmarketclonecoding.category.helper.category;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

public class CategoryRequestHelper {

    private static final String GET_ALL_CATEGORIES_URL = "/category";

    private final MockMvc mvc;

    public CategoryRequestHelper(MockMvc mvc) {
        this.mvc = mvc;
    }

    public ResultActions requestGetAllCategories() throws Exception {
        return mvc.perform(get(GET_ALL_CATEGORIES_URL));
    }
}
