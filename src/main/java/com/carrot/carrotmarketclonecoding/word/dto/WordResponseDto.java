package com.carrot.carrotmarketclonecoding.word.dto;

import com.carrot.carrotmarketclonecoding.word.domain.Word;
import lombok.*;

public class WordResponseDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class WordListResponseDto {
        private Long id;
        private String word;

        public static WordListResponseDto createWordListResponseDto(Word word) {
            return WordListResponseDto.builder()
                    .id(word.getId())
                    .word(word.getWord())
                    .build();
        }
    }
}
