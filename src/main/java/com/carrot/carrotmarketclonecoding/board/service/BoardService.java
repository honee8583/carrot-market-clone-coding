package com.carrot.carrotmarketclonecoding.board.service;

import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardDetailResponseDto;

public interface BoardService {
    Long register(BoardRegisterRequestDto inputRequestDto, Long memberId);

    BoardDetailResponseDto detail(Long boardId, String sessionId);
}
