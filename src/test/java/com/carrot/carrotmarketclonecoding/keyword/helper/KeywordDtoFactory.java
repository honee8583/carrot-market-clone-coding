package com.carrot.carrotmarketclonecoding.keyword.helper;

import com.carrot.carrotmarketclonecoding.keyword.dto.KeywordRequestDto.KeywordCreateRequestDto;
import com.carrot.carrotmarketclonecoding.keyword.dto.KeywordRequestDto.KeywordEditRequestDto;
import com.carrot.carrotmarketclonecoding.keyword.dto.KeywordResponseDto.KeywordDetailResponseDto;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class KeywordDtoFactory {

    public KeywordCreateRequestDto createKeywordRequestDto() {
        return KeywordCreateRequestDto.builder()
                .name("키보드")
                .build();
    }

    public KeywordEditRequestDto createKeywordEditRequestDto() {
        return KeywordEditRequestDto.builder()
                .name("마우스")
                .categoryId(1L)
                .minPrice(10000)
                .maxPrice(200000)
                .build();
    }

    public List<KeywordDetailResponseDto> createKeywordDetailResponseDto() {
        return Arrays.asList(
                KeywordDetailResponseDto.builder()
                        .id(1L)
                        .categoryId(1L)
                        .name("키보드")
                        .minPrice(0)
                        .maxPrice(20000)
                        .createDate(LocalDateTime.now().minusDays(1))
                        .updateDate(LocalDateTime.now())
                        .build(),
                KeywordDetailResponseDto.builder()
                        .id(2L)
                        .categoryId(1L)
                        .name("마우스")
                        .minPrice(10000)
                        .maxPrice(20000)
                        .createDate(LocalDateTime.now().minusDays(1))
                        .updateDate(LocalDateTime.now())
                        .build()
        );
    }

}
