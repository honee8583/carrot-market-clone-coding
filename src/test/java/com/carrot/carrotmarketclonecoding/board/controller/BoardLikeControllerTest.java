package com.carrot.carrotmarketclonecoding.board.controller;

import static com.carrot.carrotmarketclonecoding.board.displayname.BoardLikeTestDisplayNames.MESSAGE.*;
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

import com.carrot.carrotmarketclonecoding.auth.config.WithCustomMockUser;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardSearchResponseDto;
import com.carrot.carrotmarketclonecoding.board.service.impl.BoardLikeServiceImpl;
import com.carrot.carrotmarketclonecoding.common.exception.BoardNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberAlreadyLikedBoardException;
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

@WithCustomMockUser
@WebMvcTest(controllers = BoardLikeController.class)
class BoardLikeControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BoardLikeServiceImpl boardLikeService;

    @Nested
    @DisplayName(ADD_BOARD_LIKE_CONTROLLER_TEST)
    class AddBoardLike {

        @Test
        @DisplayName(SUCCESS)
        void addBoardLikeSuccess() throws Exception {
            // given
            // when
            doNothing().when(boardLikeService).add(anyLong(), anyLong());

            // then
            mvc.perform(post("/board/like/{id}", 1L)
                            .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status", equalTo(201)))
                    .andExpect(jsonPath("$.result", equalTo(true)))
                    .andExpect(jsonPath("$.message", equalTo(ADD_BOARD_LIKE_SUCCESS.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName(FAIL_BOARD_NOT_FOUND)
        void addBoardLikeFailBoardNotFound() throws Exception {
            // given
            // when
            doThrow(BoardNotFoundException.class).when(boardLikeService).add(anyLong(), anyLong());

            // then
            mvc.perform(post("/board/like/{id}", 1L)
                            .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(BOARD_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void addBoardLikeFailMemberNotFound() throws Exception {
            // given
            // when
            doThrow(MemberNotFoundException.class).when(boardLikeService).add(anyLong(), anyLong());

            // then
            mvc.perform(post("/board/like/{id}", 1L)
                            .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status", equalTo(401)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(MEMBER_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName(FAIL_BOARD_LIKE_ALREADY_ADDED)
        void addBoardLikeFailMemberAlreadyLikedBoard() throws Exception {
            // given
            // when
            doThrow(MemberAlreadyLikedBoardException.class).when(boardLikeService).add(anyLong(), anyLong());

            // then
            mvc.perform(post("/board/like/{id}", 1L)
                            .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(MEMBER_ALREADY_LIKED_BOARD.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }
    }

    @Nested
    @DisplayName(GET_BOARD_LIKE_LIST_CONTROLLER_TEST)
    class MemberLikedBoards {

        @Test
        @DisplayName(SUCCESS)
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
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
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