package com.carrot.carrotmarketclonecoding.word.service;

import com.carrot.carrotmarketclonecoding.word.dto.WordRequestDto.WordRegisterRequestDto;

public interface WordService {
    void add(Long memberId, WordRegisterRequestDto registerRequestDto);
}
