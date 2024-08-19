package com.carrot.carrotmarketclonecoding.board.service;

import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardUpdateRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardDetailResponseDto;

public interface BoardService {
    Long register(BoardRegisterRequestDto registerRequestDto, Long memberId, boolean tmp);

    BoardDetailResponseDto detail(Long boardId, String sessionId);

    void update(BoardUpdateRequestDto updateRequestDto, Long boardId, Long memberId);

    void delete(Long boardId, Long memberId);
}
