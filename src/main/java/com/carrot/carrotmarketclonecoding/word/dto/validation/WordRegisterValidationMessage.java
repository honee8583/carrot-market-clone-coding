package com.carrot.carrotmarketclonecoding.word.dto.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WordRegisterValidationMessage {
    WORD_NOT_VALID(MESSAGE.WORD_NOT_VALID);

    private final String message;

    public static class MESSAGE {
        public static final String WORD_NOT_VALID = "문구내용은 필수 입력사항입니다!";
    }
}
