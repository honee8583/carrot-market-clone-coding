package com.carrot.carrotmarketclonecoding.board.repository;

import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardSearchRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.MyBoardSearchRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardSearchResponseDto;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardRepositoryCustom {
    Page<BoardSearchResponseDto> findAllBySearchRequestDto(BoardSearchRequestDto searchRequestDto, Pageable pageable);

    Page<BoardSearchResponseDto> searchMemberLikedBoards(Member member, Pageable pageable);

    Page<BoardSearchResponseDto> findAllByStatusOrHide(Member member, MyBoardSearchRequestDto searchRequestDto, Pageable pageable);
}
