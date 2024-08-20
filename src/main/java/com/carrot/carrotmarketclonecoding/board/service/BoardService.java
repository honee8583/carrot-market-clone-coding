package com.carrot.carrotmarketclonecoding.board.service;

import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardSearchRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardUpdateRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardDetailResponseDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardSearchResponseDto;
import com.carrot.carrotmarketclonecoding.common.response.PageResponseDto;
import org.springframework.data.domain.Pageable;

public interface BoardService {
    Long register(BoardRegisterRequestDto registerRequestDto, Long memberId, boolean tmp);

    BoardDetailResponseDto detail(Long boardId, String sessionId);

    BoardDetailResponseDto tmpBoardDetail(Long memberId);

    PageResponseDto<BoardSearchResponseDto> search(BoardSearchRequestDto searchRequestDto, Pageable pageable);

    void update(BoardUpdateRequestDto updateRequestDto, Long boardId, Long memberId);

    void delete(Long boardId, Long memberId);
}
