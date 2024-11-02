package com.carrot.carrotmarketclonecoding.category.controller;

import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.GET_CATEGORIES_SUCCESS;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrot.carrotmarketclonecoding.auth.config.WithCustomMockUser;
import com.carrot.carrotmarketclonecoding.category.dto.CategoryResponseDto;
import com.carrot.carrotmarketclonecoding.category.helper.category.CategoryTestHelper;
import com.carrot.carrotmarketclonecoding.category.service.impl.CategoryServiceImpl;
import com.carrot.carrotmarketclonecoding.util.RestDocsTestUtil;
import com.carrot.carrotmarketclonecoding.util.ResultFields;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WithCustomMockUser
@WebMvcTest(controllers = CategoryController.class)
class CategoryControllerTest extends RestDocsTestUtil {

    private CategoryTestHelper testHelper;

    @MockBean
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        this.testHelper = new CategoryTestHelper(mvc, restDocs);
    }

    @Test
    @DisplayName("카테고리 전체 목록 조회 테스트")
    void getAllCategory() throws Exception {
        // given
        List<CategoryResponseDto> categories = Arrays.asList(
                new CategoryResponseDto(1L, "category1"),
                new CategoryResponseDto(2L, "category2"),
                new CategoryResponseDto(3L, "category3")
        );

        ResultFields resultFields = ResultFields.builder()
                .resultMatcher(status().isOk())
                .status(200)
                .result(true)
                .message(GET_CATEGORIES_SUCCESS.getMessage())
                .build();

        // when
        when(categoryService.getAllCategory()).thenReturn(categories);

        // then
        testHelper.assertGetAllCategories(resultFields);
    }
}