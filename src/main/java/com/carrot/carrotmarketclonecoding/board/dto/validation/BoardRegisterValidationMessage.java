package com.carrot.carrotmarketclonecoding.board.dto.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BoardRegisterValidationMessage {
    TITLE_NOT_VALID(MESSAGE.TITLE_NOT_VALID),
    CATEGORY_NOT_VALID(MESSAGE.CATEGORY_NOT_VALID),
    PRICE_NOT_VALID(MESSAGE.PRICE_NOT_VALID),
    SUGGEST_NOT_VALID(MESSAGE.SUGGEST_NOT_VALID),
    DESCRIPTION_NOT_VALID(MESSAGE.DESCRIPTION_NOT_VALID),
    PLACE_NOT_VALID(MESSAGE.PLACE_NOT_VALID);

    private final String message;

    public static class MESSAGE {
        public static final String TITLE_NOT_VALID = "제목은 필수 입력사항입니다!";
        public static final String CATEGORY_NOT_VALID = "카테고리는 필수 입력사항입니다!";
        public static final String PRICE_NOT_VALID = "가격은 필수 입력사항입니다!";
        public static final String SUGGEST_NOT_VALID = "거래제안받기여부는 필수 입력사항입니다!";
        public static final String DESCRIPTION_NOT_VALID = "상품설명은 필수 입력값입니다!";
        public static final String DESCRIPTION_OVER_LENGTH = "상품 설명은 300글자 이내여야 합니다!";
        public static final String PLACE_NOT_VALID = "거래희망장소는 필수 입력사항입니다!";
    }
}
