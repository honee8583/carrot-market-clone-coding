package com.carrot.carrotmarketclonecoding.word.service;

import com.carrot.carrotmarketclonecoding.word.dto.WordRequestDto.WordRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.word.dto.WordResponseDto.WordListResponseDto;
import java.util.List;

public interface WordService {
    void add(Long memberId, WordRegisterRequestDto registerRequestDto);

    List<WordListResponseDto> list(Long memberId);
}
