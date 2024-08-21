package com.carrot.carrotmarketclonecoding.board.controller;

import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.ADD_BOARD_LIKE_SUCCESS;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.GET_MEMBER_LIKED_BOARDS_SUCCESS;

import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardSearchResponseDto;
import com.carrot.carrotmarketclonecoding.board.service.BoardLikeService;
import com.carrot.carrotmarketclonecoding.common.response.PageResponseDto;
import com.carrot.carrotmarketclonecoding.common.response.ResponseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/board/like")
@RequiredArgsConstructor
public class BoardLikeController {
    private final BoardLikeService boardLikeService;

    @PostMapping("/{id}")
    public ResponseEntity<?> addBoardLike(@PathVariable("id") Long boardId) {
        // TODO memberId -> JWT.getMemberId()
        Long memberId = 1L;
        boardLikeService.add(boardId, memberId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseResult.success(HttpStatus.CREATED, ADD_BOARD_LIKE_SUCCESS.getMessage(), null));
    }

    @GetMapping
    public ResponseEntity<?> memberLikedBoards(@PageableDefault(size = 10) Pageable pageable) {
        // TODO memberId -> JWT.getMemberId()
        Long memberId = 1L;
        PageResponseDto<BoardSearchResponseDto> result = boardLikeService.getMemberLikedBoards(memberId,
                pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, GET_MEMBER_LIKED_BOARDS_SUCCESS.getMessage(), result));
    }
}
