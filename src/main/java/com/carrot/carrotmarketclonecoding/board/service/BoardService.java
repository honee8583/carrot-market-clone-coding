package com.carrot.carrotmarketclonecoding.board.service;

import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardSearchRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardUpdateRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.MyBoardSearchRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardDetailResponseDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardSearchResponseDto;
import com.carrot.carrotmarketclonecoding.common.response.PageResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface BoardService {
    Long register(Long authId, BoardRegisterRequestDto registerRequestDto, MultipartFile[] pictures, boolean tmp);

    BoardDetailResponseDto detail(Long boardId, HttpServletRequest request);

    BoardDetailResponseDto tmpBoardDetail(Long memberId);

    PageResponseDto<BoardSearchResponseDto> search(Long authId, BoardSearchRequestDto searchRequestDto, Pageable pageable);

    PageResponseDto<BoardSearchResponseDto> searchMyBoards(Long authId, MyBoardSearchRequestDto searchRequestDto, Pageable pageable);

    void update(Long boardId, Long authId, BoardUpdateRequestDto updateRequestDto, MultipartFile[] newPictures);

    void delete(Long boardId, Long authId);
}
