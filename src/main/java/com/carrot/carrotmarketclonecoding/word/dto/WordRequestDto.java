package com.carrot.carrotmarketclonecoding.word.dto;

import static com.carrot.carrotmarketclonecoding.word.dto.validation.WordRegisterValidationMessage.MESSAGE.*;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WordRequestDto {
    @NotEmpty(message = WORD_NOT_VALID)
    private String word;
}
