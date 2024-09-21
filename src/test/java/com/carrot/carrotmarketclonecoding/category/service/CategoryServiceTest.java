package com.carrot.carrotmarketclonecoding.category.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.carrot.carrotmarketclonecoding.category.domain.Category;
import com.carrot.carrotmarketclonecoding.category.dto.CategoryResponseDto;
import com.carrot.carrotmarketclonecoding.category.repository.CategoryRepository;
import com.carrot.carrotmarketclonecoding.category.service.impl.CategoryServiceImpl;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("카테고리 전체 목록 조회 테스트")
    void getAllCategory() {
        // given
        List<Category> categories = Arrays.asList(
                new Category(1L, "category1"),
                new Category(2L, "category2"),
                new Category(3L, "category3")
        );

        when(categoryRepository.findAll()).thenReturn(categories);

        // when
        List<CategoryResponseDto> categoryResponse = categoryService.getAllCategory();

        // then
        assertThat(categoryResponse.size()).isEqualTo(3);
    }
}