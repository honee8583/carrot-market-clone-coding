package com.carrot.carrotmarketclonecoding.board.dto;

import com.carrot.carrotmarketclonecoding.board.domain.enums.Method;
import com.carrot.carrotmarketclonecoding.common.exception.ValidEnum;
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

        @NotEmpty(message = "제목은 필수 입력사항입니다!")
        private String title;

        @NotNull(message = "카테고리는 필수 입력사항입니다!")
        private Long categoryId;

//        @NotNull(message = "거래방식은 필수 입력사항입니다!")
        @ValidEnum(enumClass = Method.class)
        private Method method;

        @NotNull(message = "가격은 필수 입력사항입니다!")
        private Integer price;

        @NotNull(message = "거래제안받기여부는 필수 입력사항입니다!")
        private Boolean suggest;

        @Size(max = 300, message = "상품 설명은 300글자 이내여야 합니다!")
        private String description;

        @NotEmpty(message = "거래희망장소는 필수 입력사항입니다!")
        private String place;

        private Boolean tmp;
    }
}
