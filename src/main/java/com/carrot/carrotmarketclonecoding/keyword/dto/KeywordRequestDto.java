package com.carrot.carrotmarketclonecoding.keyword.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

public class KeywordRequestDto {

    @Getter
    @Setter
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeywordCreateRequestDto {
        @NotEmpty(message = "키워드명은 필수 입력값입니다!")
        private String name;
    }

    @Getter
    @Setter
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeywordEditRequestDto {
        private String name;
        private Long categoryId;
        private Integer minPrice;
        private Integer maxPrice;
    }
}
