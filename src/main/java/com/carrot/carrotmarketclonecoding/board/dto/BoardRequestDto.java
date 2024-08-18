package com.carrot.carrotmarketclonecoding.board.dto;

import static com.carrot.carrotmarketclonecoding.board.dto.validation.BoardRegisterValidationMessage.MESSAGE.*;

import com.carrot.carrotmarketclonecoding.board.domain.enums.Method;
import com.carrot.carrotmarketclonecoding.common.validation.ValidEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

public class BoardRequestDto {

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardRegisterRequestDto {
        private MultipartFile[] pictures;

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

        public void setPriceZeroIfMethodIsShare() {
            if (this.method == Method.SHARE) {
                this.price = 0;
            }
        }
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

        private MultipartFile[] newPictures;
        private Long[] removePictures;
    }
}
