package com.carrot.carrotmarketclonecoding.board.service;

import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardSearchResponseDto;
import com.carrot.carrotmarketclonecoding.common.response.PageResponseDto;
import org.springframework.data.domain.Pageable;

public interface BoardLikeService {
    void add(Long boardId, Long memberId);

    PageResponseDto<BoardSearchResponseDto> getMemberLikedBoards(Long memberId, Pageable pageable);
}
