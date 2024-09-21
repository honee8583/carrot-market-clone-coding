package com.carrot.carrotmarketclonecoding.category.controller;

import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.GET_CATEGORIES_SUCCESS;

import com.carrot.carrotmarketclonecoding.category.dto.CategoryResponseDto;
import com.carrot.carrotmarketclonecoding.category.service.CategoryService;
import com.carrot.carrotmarketclonecoding.common.response.ResponseResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/category")
    public ResponseEntity<?> getAllCategory() {
        List<CategoryResponseDto> categories = categoryService.getAllCategory();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, GET_CATEGORIES_SUCCESS.getMessage(), categories));
    }
}
