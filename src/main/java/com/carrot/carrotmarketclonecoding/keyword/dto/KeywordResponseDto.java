package com.carrot.carrotmarketclonecoding.keyword.dto;

import com.carrot.carrotmarketclonecoding.keyword.domain.Keyword;
import java.time.LocalDateTime;
import lombok.*;

public class KeywordResponseDto {

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeywordDetailResponseDto {
        private Long id;
        private Long categoryId;
        private String name;
        private Integer minPrice;
        private Integer maxPrice;
        private LocalDateTime createDate;
        private LocalDateTime updateDate;

        public static KeywordDetailResponseDto createDetail(Keyword keyword) {
            return KeywordDetailResponseDto.builder()
                    .id(keyword.getId())
                    .categoryId(keyword.getCategoryId())
                    .name(keyword.getName())
                    .minPrice(keyword.getMinPrice())
                    .maxPrice(keyword.getMaxPrice())
                    .createDate(keyword.getCreateDate())
                    .updateDate(keyword.getUpdateDate())
                    .build();
        }
    }
}
