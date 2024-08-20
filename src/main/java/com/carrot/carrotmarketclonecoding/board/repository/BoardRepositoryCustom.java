package com.carrot.carrotmarketclonecoding.board.repository;

import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardSearchRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardSearchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardRepositoryCustom {
    Page<BoardSearchResponseDto> searchBoards(BoardSearchRequestDto searchRequestDto, Pageable pageable);
}
