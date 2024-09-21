package com.carrot.carrotmarketclonecoding.category.controller;

import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.GET_CATEGORIES_SUCCESS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrot.carrotmarketclonecoding.auth.config.WithCustomMockUser;
import com.carrot.carrotmarketclonecoding.category.dto.CategoryResponseDto;
import com.carrot.carrotmarketclonecoding.category.service.impl.CategoryServiceImpl;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WithCustomMockUser
@WebMvcTest(controllers = CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("카테고리 전체 목록 조회 테스트")
    void getAllCategory() throws Exception {
        // given
        List<CategoryResponseDto> categories = Arrays.asList(
                new CategoryResponseDto(1L, "category1"),
                new CategoryResponseDto(2L, "category2"),
                new CategoryResponseDto(3L, "category3")
        );

        // when
        when(categoryService.getAllCategory()).thenReturn(categories);

        // then
        mvc.perform(get("/category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", equalTo(200)))
                .andExpect(jsonPath("$.result", equalTo(true)))
                .andExpect(jsonPath("$.message", equalTo(GET_CATEGORIES_SUCCESS.getMessage())))
                .andExpect(jsonPath("$.data.size()", equalTo(3)));

    }
}