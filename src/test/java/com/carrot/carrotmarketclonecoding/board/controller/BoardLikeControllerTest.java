package com.carrot.carrotmarketclonecoding.board.controller;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.*;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.*;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardSearchResponseDto;
import com.carrot.carrotmarketclonecoding.board.service.impl.BoardLikeServiceImpl;
import com.carrot.carrotmarketclonecoding.common.exception.BoardNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.response.PageResponseDto;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BoardLikeController.class)
class BoardLikeControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BoardLikeServiceImpl boardLikeService;

    @Nested
    @DisplayName("관심게시글 등록 컨트롤러 테스트")
    class AddBoardLike {

        @Test
        @DisplayName("성공")
        void addBoardLikeSuccess() throws Exception {
            // given
            // when
            doNothing().when(boardLikeService).add(anyLong(), anyLong());

            // then
            mvc.perform(post("/board/like/{id}", 1L))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status", equalTo(201)))
                    .andExpect(jsonPath("$.result", equalTo(true)))
                    .andExpect(jsonPath("$.message", equalTo(ADD_BOARD_LIKE_SUCCESS.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName("실패 - 게시글이 존재하지 않음")
        void addBoardLikeFailBoardNotFound() throws Exception {
            // given
            // when
            doThrow(BoardNotFoundException.class).when(boardLikeService).add(anyLong(), anyLong());

            // then
            mvc.perform(post("/board/like/{id}", 1L))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(BOARD_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName("실패 - 사용자가 존재하지 않음")
        void addBoardLikeFailMemberNotFound() throws Exception {
            // given
            // when
            doThrow(MemberNotFoundException.class).when(boardLikeService).add(anyLong(), anyLong());

            // then
            mvc.perform(post("/board/like/{id}", 1L))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status", equalTo(401)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(MEMBER_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }
    }

    @Nested
    @DisplayName("관심게시글 목록 조회 컨트롤러 테스트")
    class MemberLikedBoards {

        @Test
        @DisplayName("성공")
        void memberLikedBoardsSuccess() throws Exception {
            // given
            List<BoardSearchResponseDto> response = Arrays.asList(
                    new BoardSearchResponseDto(),
                    new BoardSearchResponseDto());
            PageResponseDto<BoardSearchResponseDto> result = new PageResponseDto<>(
                    new PageImpl<>(response, PageRequest.of(0, 10), response.size()));

            // when
            when(boardLikeService.getMemberLikedBoards(anyLong(), any())).thenReturn(result);

            // then
            mvc.perform(get("/board/like")
                    .param("page", "0"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", equalTo(200)))
                    .andExpect(jsonPath("$.result", equalTo(true)))
                    .andExpect(jsonPath("$.message", equalTo(GET_MEMBER_LIKED_BOARDS_SUCCESS.getMessage())))
                    .andExpect(jsonPath("$.data.totalPage", equalTo(1)))
                    .andExpect(jsonPath("$.data.totalElements", equalTo(2)))
                    .andExpect(jsonPath("$.data.first", equalTo(true)))
                    .andExpect(jsonPath("$.data.last", equalTo(true)))
                    .andExpect(jsonPath("$.data.numberOfElements", equalTo(2)));
        }

        @Test
        @DisplayName("실패 - 사용자 존재하지 않음")
        void memberLikedBoardsFailMemberNotFound() throws Exception {
            // given
            // when
            doThrow(MemberNotFoundException.class).when(boardLikeService).getMemberLikedBoards(anyLong(), any());

            // then
            mvc.perform(get("/board/like")
                    .param("page", "0"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status", equalTo(401)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(MEMBER_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }
    }
}