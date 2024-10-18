package com.carrot.carrotmarketclonecoding.board.dto;

import static com.carrot.carrotmarketclonecoding.board.dto.validation.BoardRegisterValidationMessage.MESSAGE.*;

import com.carrot.carrotmarketclonecoding.board.domain.enums.Method;
import com.carrot.carrotmarketclonecoding.board.domain.enums.SearchOrder;
import com.carrot.carrotmarketclonecoding.board.domain.enums.Status;
import com.carrot.carrotmarketclonecoding.common.validation.ValidEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

public class BoardRequestDto {

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardRegisterRequestDto {

        @NotEmpty(message = TITLE_NOT_VALID)
        private String title;

        @NotNull(message = CATEGORY_NOT_VALID)
        private Long categoryId;

        @ValidEnum(enumClass = Method.class)
        private Method method;

        @NotNull(message = PRICE_NOT_VALID)
        private Integer price;

        @NotNull(message = SUGGEST_NOT_VALID)
        private Boolean suggest;

        @NotEmpty(message = DESCRIPTION_NOT_VALID)
        @Size(max = 300, message = DESCRIPTION_OVER_LENGTH)
        private String description;

        @NotEmpty(message = PLACE_NOT_VALID)
        private String place;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class  BoardUpdateRequestDto {
        @NotEmpty(message = TITLE_NOT_VALID)
        private String title;

        @NotNull(message = CATEGORY_NOT_VALID)
        private Long categoryId;

        @ValidEnum(enumClass = Method.class)
        private Method method;

        @NotNull(message = PRICE_NOT_VALID)
        private Integer price;

        @NotNull(message = SUGGEST_NOT_VALID)
        private Boolean suggest;

        @NotEmpty(message = DESCRIPTION_NOT_VALID)
        @Size(max = 300, message = DESCRIPTION_OVER_LENGTH)
        private String description;

        @NotEmpty(message = PLACE_NOT_VALID)
        private String place;

        private Long[] removePictures;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardSearchRequestDto {
        private Long categoryId;
        private String keyword;
        private Integer minPrice;
        private Integer maxPrice;
        private SearchOrder order;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyBoardSearchRequestDto {
        private Status status;
        private Boolean hide;
    }
}
