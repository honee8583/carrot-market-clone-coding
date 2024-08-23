package com.carrot.carrotmarketclonecoding.word.dto;

import static com.carrot.carrotmarketclonecoding.word.dto.validation.WordRegisterValidationMessage.MESSAGE.*;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

public class WordRequestDto {

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WordRegisterRequestDto {
        @NotEmpty(message = WORD_NOT_VALID)
        private String word;
    }
}
