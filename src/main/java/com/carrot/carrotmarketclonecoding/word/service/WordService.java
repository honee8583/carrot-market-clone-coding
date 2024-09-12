package com.carrot.carrotmarketclonecoding.word.service;

import com.carrot.carrotmarketclonecoding.word.dto.WordRequestDto;
import com.carrot.carrotmarketclonecoding.word.dto.WordResponseDto.WordListResponseDto;
import java.util.List;

public interface WordService {
    void add(Long authId, WordRequestDto wordRequestDto);

    List<WordListResponseDto> list(Long authId);

    void update(Long authId, Long wordId, WordRequestDto wordRequestDto);

    void remove(Long authId, Long wordId);
}
