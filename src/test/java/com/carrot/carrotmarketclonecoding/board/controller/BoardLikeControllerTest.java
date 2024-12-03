package com.carrot.carrotmarketclonecoding.board.controller;

import static com.carrot.carrotmarketclonecoding.board.displayname.BoardLikeTestDisplayNames.MESSAGE.*;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.*;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrot.carrotmarketclonecoding.ControllerTest;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardSearchResponseDto;
import com.carrot.carrotmarketclonecoding.board.helper.boardlike.BoardLikeDtoFactory;
import com.carrot.carrotmarketclonecoding.board.helper.boardlike.BoardLikeTestHelper;
import com.carrot.carrotmarketclonecoding.common.exception.BoardNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberAlreadyLikedBoardException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.response.PageResponseDto;
import com.carrot.carrotmarketclonecoding.util.ResultFields;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class BoardLikeControllerTest extends ControllerTest {

    private BoardLikeTestHelper testHelper;

    @BeforeEach
    public void setUp() {
        this.testHelper = new BoardLikeTestHelper(mvc, restDocs);
    }

    @Nested
    @DisplayName(ADD_BOARD_LIKE_CONTROLLER_TEST)
    class AddBoardLike {

        @Test
        @DisplayName(SUCCESS)
        void addBoardLikeSuccess() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isCreated())
                    .status(201)
                    .result(true)
                    .message(ADD_BOARD_LIKE_SUCCESS.getMessage())
                    .build();

            // when
            doNothing().when(boardLikeService).add(anyLong(), anyLong());

            // then

            testHelper.assertLikeBoard(resultFields);
        }

        @Test
        @DisplayName(FAIL_BOARD_NOT_FOUND)
        void addBoardLikeFailBoardNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isBadRequest())
                    .status(400)
                    .result(false)
                    .message(BOARD_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(BoardNotFoundException.class).when(boardLikeService).add(anyLong(), anyLong());

            // then
            testHelper.assertLikeBoard(resultFields);
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void addBoardLikeFailMemberNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(MemberNotFoundException.class).when(boardLikeService).add(anyLong(), anyLong());

            // then
            testHelper.assertLikeBoard(resultFields);
        }

        @Test
        @DisplayName(FAIL_BOARD_LIKE_ALREADY_ADDED)
        void addBoardLikeFailMemberAlreadyLikedBoard() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isBadRequest())
                    .status(400)
                    .result(false)
                    .message(MEMBER_ALREADY_LIKED_BOARD.getMessage())
                    .build();

            // when
            doThrow(MemberAlreadyLikedBoardException.class).when(boardLikeService).add(anyLong(), anyLong());

            // then
            testHelper.assertLikeBoard(resultFields);
        }
    }

    @Nested
    @DisplayName(GET_BOARD_LIKE_LIST_CONTROLLER_TEST)
    class UserLikedBoards {

        @Autowired
        private BoardLikeDtoFactory dtoFactory;

        @Test
        @DisplayName(SUCCESS)
        void userLikedBoardsSuccess() throws Exception {
            // given
            List<BoardSearchResponseDto> response = dtoFactory.createBoardSearchResponseDtos();
            PageResponseDto<BoardSearchResponseDto> result = new PageResponseDto<>(
                    new PageImpl<>(response, PageRequest.of(0, 10), response.size())
            );

            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(GET_MEMBER_LIKED_BOARDS_SUCCESS.getMessage())
                    .build();

            // when
            when(boardLikeService.getMemberLikedBoards(anyLong(), any())).thenReturn(result);

            // then
            testHelper.assertGetUserLikedBoardsSuccess(resultFields);
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void userLikedBoardsFailMemberNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(MemberNotFoundException.class).when(boardLikeService).getMemberLikedBoards(anyLong(), any());

            // then
            testHelper.assertGetUserLikedBoardsFailed(resultFields);
        }
    }
}