package com.carrot.carrotmarketclonecoding.board.controller;

import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.BOARD_GET_DETAIL_SUCCESS;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.BOARD_REGISTER_SUCCESS;

import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardDetailResponseDto;
import com.carrot.carrotmarketclonecoding.board.service.BoardService;
import com.carrot.carrotmarketclonecoding.common.response.ResponseResult;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @PostMapping("/board/register")
    public ResponseEntity<?> register(@ModelAttribute @Valid BoardRegisterRequestDto registerRequestDto) {
        // TODO memberId -> JWT.getMemberId()
        Long memberId = 1L;

        boardService.register(registerRequestDto, memberId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseResult.success(HttpStatus.CREATED, BOARD_REGISTER_SUCCESS.getMessage(), null));
    }

    @GetMapping("/board/{id}")
    public ResponseEntity<?> detail(@PathVariable("id") Long id, HttpSession session) {
        BoardDetailResponseDto boardDetail = boardService.detail(id, session.getId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, BOARD_GET_DETAIL_SUCCESS.getMessage(), boardDetail));
    }
}
