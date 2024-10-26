package com.carrot.carrotmarketclonecoding.board.controller;

import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.*;

import com.carrot.carrotmarketclonecoding.auth.dto.LoginUser;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardSearchRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardUpdateRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.MyBoardSearchRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardDetailResponseDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardSearchResponseDto;
import com.carrot.carrotmarketclonecoding.board.service.BoardService;
import com.carrot.carrotmarketclonecoding.common.response.PageResponseDto;
import com.carrot.carrotmarketclonecoding.common.response.ResponseResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@AuthenticationPrincipal LoginUser loginUser,
                                      @RequestPart(value = "pictures", required = false) MultipartFile[] pictures,
                                      @RequestPart("registerRequest") @Valid BoardRegisterRequestDto registerRequestDto) {
        Long authId = Long.parseLong(loginUser.getUsername());
        boardService.register(authId, registerRequestDto, pictures, false);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseResult.success(HttpStatus.CREATED, BOARD_REGISTER_SUCCESS.getMessage(), null));
    }

    @PostMapping("/register/tmp")
    public ResponseEntity<?> registerTmp(@AuthenticationPrincipal LoginUser loginUser,
                                         @RequestPart(value = "pictures", required = false) MultipartFile[] pictures,
                                         @RequestPart("registerRequest") BoardRegisterRequestDto registerRequestDto) {
        Long authId = Long.parseLong(loginUser.getUsername());
        boardService.register(authId, registerRequestDto, pictures, true);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseResult.success(HttpStatus.CREATED, BOARD_REGISTER_TEMPORARY_SUCCESS.getMessage(), null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable("id") Long boardId, HttpServletRequest request) {
        BoardDetailResponseDto boardDetail = boardService.getBoardDetail(boardId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, BOARD_GET_DETAIL_SUCCESS.getMessage(), boardDetail));
    }

    @GetMapping("/tmp")
    public ResponseEntity<?> tmpDetail(@AuthenticationPrincipal LoginUser loginUser) {
        Long authId = Long.parseLong(loginUser.getUsername());
        BoardDetailResponseDto boardDetail = boardService.getTmpBoardDetail(authId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, BOARD_GET_TMP_SUCCESS.getMessage(), boardDetail));
    }

    @GetMapping
    public ResponseEntity<?> search(@AuthenticationPrincipal LoginUser loginUser, BoardSearchRequestDto searchRequestDto, @PageableDefault(size = 10) Pageable pageable) {
        Long authId = Long.parseLong(loginUser.getUsername());
        PageResponseDto<BoardSearchResponseDto> boards = boardService.search(authId, searchRequestDto, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, SEARCH_BOARDS_SUCCESS.getMessage(), boards));
    }

    @GetMapping("/my")
    public ResponseEntity<?> searchMyBoards(@AuthenticationPrincipal LoginUser loginUser, MyBoardSearchRequestDto searchRequestDto, @PageableDefault(size = 10) Pageable pageable) {
        Long authId = Long.parseLong(loginUser.getUsername());
        PageResponseDto<BoardSearchResponseDto> boards = boardService.searchMyBoards(authId, searchRequestDto, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, SEARCH_BOARDS_SUCCESS.getMessage(), boards));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@AuthenticationPrincipal LoginUser loginUser,
                                    @PathVariable("id") Long boardId,
                                    @RequestPart("newPictures") MultipartFile[] newPictures,
                                    @RequestPart("updateRequest") @Valid BoardUpdateRequestDto updateRequestDto) {
        Long authId = Long.parseLong(loginUser.getUsername());
        boardService.update(boardId, authId, updateRequestDto, newPictures);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, BOARD_UPDATE_SUCCESS.getMessage(), null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal LoginUser loginUser, @PathVariable("id") Long boardId) {
        Long authId = Long.parseLong(loginUser.getUsername());
        boardService.delete(boardId, authId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, BOARD_DELETE_SUCCESS.getMessage(), null));
    }
}
