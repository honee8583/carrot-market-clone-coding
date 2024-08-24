package com.carrot.carrotmarketclonecoding.word.service;

import com.carrot.carrotmarketclonecoding.word.dto.WordRequestDto;
import com.carrot.carrotmarketclonecoding.word.dto.WordResponseDto.WordListResponseDto;
import java.util.List;

public interface WordService {
    void add(Long memberId, WordRequestDto wordRequestDto);

    List<WordListResponseDto> list(Long memberId);

    void update(Long memberId, Long wordId, WordRequestDto wordRequestDto);
}
