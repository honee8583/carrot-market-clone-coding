package com.carrot.carrotmarketclonecoding.board.controller;

import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.*;

import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardSearchRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardUpdateRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardDetailResponseDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardSearchResponseDto;
import com.carrot.carrotmarketclonecoding.board.service.BoardService;
import com.carrot.carrotmarketclonecoding.common.response.PageResponseDto;
import com.carrot.carrotmarketclonecoding.common.response.ResponseResult;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@ModelAttribute @Valid BoardRegisterRequestDto registerRequestDto) {
        // TODO memberId -> JWT.getMemberId()
        Long memberId = 1L;
        boardService.register(registerRequestDto, memberId, false);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseResult.success(HttpStatus.CREATED, BOARD_REGISTER_SUCCESS.getMessage(), null));
    }

    @PostMapping("/register/tmp")
    public ResponseEntity<?> registerTmp(@ModelAttribute BoardRegisterRequestDto registerRequestDto) {
        // TODO memberId -> JWT.getMemberId()
        Long memberId = 1L;
        boardService.register(registerRequestDto, memberId, true);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseResult.success(HttpStatus.CREATED, BOARD_REGISTER_TEMPORARY_SUCCESS.getMessage(), null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable("id") Long boardId, HttpSession session) {
        BoardDetailResponseDto boardDetail = boardService.detail(boardId, session.getId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, BOARD_GET_DETAIL_SUCCESS.getMessage(), boardDetail));
    }

    @GetMapping("/tmp")
    public ResponseEntity<?> tmpDetail() {
        // TODO memberId -> JWT.getMemberId()
        Long memberId = 1L;
        BoardDetailResponseDto boardDetail = boardService.tmpBoardDetail(memberId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, BOARD_GET_TMP_SUCCESS.getMessage(), boardDetail));
    }

    @GetMapping
    public ResponseEntity<?> search(BoardSearchRequestDto searchRequestDto, @PageableDefault(size = 10) Pageable pageable) {
        PageResponseDto<BoardSearchResponseDto> boards = boardService.search(null, searchRequestDto, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, SEARCH_BOARDS_SUCCESS.getMessage(), boards));
    }

    @GetMapping("/status")
    public ResponseEntity<?> searchBoardsByStatus(BoardSearchRequestDto searchRequestDto, @PageableDefault(size = 10) Pageable pageable) {
        // TODO memberId -> JWT.getMemberId()
        Long memberId = 1L;
        PageResponseDto<BoardSearchResponseDto> boards = boardService.search(memberId, searchRequestDto, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, SEARCH_BOARDS_SUCCESS.getMessage(), boards));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long boardId, @ModelAttribute @Valid BoardUpdateRequestDto updateRequestDto) {
        // TODO memberId -> JWT.getMemberId()
        Long memberId = 1L;
        boardService.update(updateRequestDto, boardId, memberId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, BOARD_UPDATE_SUCCESS.getMessage(), null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long boardId) {
        // TODO memberId -> JWT.getMemberId()
        Long memberId = 1L;
        boardService.delete(boardId, memberId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, BOARD_DELETE_SUCCESS.getMessage(), null));
    }
}
