package com.carrot.carrotmarketclonecoding.board.service;

import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardRegisterRequestDto;

public interface BoardService {
    Long register(BoardRegisterRequestDto inputRequestDto, Long memberId);
}
