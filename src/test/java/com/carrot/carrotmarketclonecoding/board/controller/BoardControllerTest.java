package com.carrot.carrotmarketclonecoding.board.controller;

import static com.carrot.carrotmarketclonecoding.board.displayname.BoardTestDisplayNames.MESSAGE.*;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.*;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrot.carrotmarketclonecoding.auth.config.WithCustomMockUser;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardUpdateRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardDetailResponseDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardSearchResponseDto;
import com.carrot.carrotmarketclonecoding.board.helper.board.BoardDtoFactory;
import com.carrot.carrotmarketclonecoding.board.helper.board.BoardTestHelper;
import com.carrot.carrotmarketclonecoding.board.service.impl.BoardServiceImpl;
import com.carrot.carrotmarketclonecoding.common.exception.BoardNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.CategoryNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.FileUploadLimitException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.UnauthorizedAccessException;
import com.carrot.carrotmarketclonecoding.common.response.PageResponseDto;
import com.carrot.carrotmarketclonecoding.util.RestDocsTestUtil;
import com.carrot.carrotmarketclonecoding.util.ResultFields;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;

@Import(value = {
        BoardTestHelper.class,
        BoardDtoFactory.class
})
@WithCustomMockUser
@WebMvcTest(controllers = BoardController.class)
class BoardControllerTest extends RestDocsTestUtil {

    private BoardTestHelper testHelper;

    @Autowired
    private BoardDtoFactory dtoFactory;

    @MockBean
    private BoardServiceImpl boardService;

    @BeforeEach
    public void setUp() {
        this.testHelper = new BoardTestHelper(mvc, restDocs);
    }

    @Nested
    @DisplayName(BOARD_REGISTER_CONTROLLER_TEST)
    class BoardRegister {

        private MockMultipartFile[] pictures;
        private MockMultipartFile registerRequest;

        @BeforeEach
        public void setUp() throws Exception {
            this.pictures = dtoFactory.createMockMultipartFiles("pictures", 2);
            this.registerRequest = dtoFactory.createRegisterRequestMultipartFile(
                    dtoFactory.createRegisterRequestDto()
            );
        }

        @Test
        @DisplayName(SUCCESS)
        void boardRegisterSuccess() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isCreated())
                    .status(201)
                    .result(true)
                    .message(BOARD_REGISTER_SUCCESS.getMessage())
                    .build();

            // when
            when(boardService.register(any(), any(), any(), anyBoolean())).thenReturn(1L);

            // then
            testHelper.assertRegisterBoardSuccess(resultFields, registerRequest, pictures);
        }

        @Test
        @DisplayName(FAIL_FILE_COUNT_OVER_10)
        void boardRegisterFileUploadLimitExceeded() throws Exception {
            // given
            this.pictures = dtoFactory.createMockMultipartFiles("pictures", 30);
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isInternalServerError())
                    .status(500)
                    .result(false)
                    .message(FILE_UPLOAD_LIMIT.getMessage())
                    .build();

            // when
            when(boardService.register(anyLong(), any(), any(), anyBoolean())).thenThrow(FileUploadLimitException.class);

            // then
            testHelper.assertRegisterBoardFailed(resultFields, registerRequest, pictures);
        }

        @Test
        @DisplayName(FAIL_INPUT_NOT_VALID)
        void boardRegisterValidationFailed() throws Exception {
            // given
            registerRequest = dtoFactory.createRegisterRequestMultipartFile(new BoardRegisterRequestDto());
            Map<String, String> errorMessages = dtoFactory.createInputInvalidResponseData();
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isBadRequest())
                    .status(400)
                    .result(false)
                    .message(INPUT_NOT_VALID.getMessage())
                    .build();

            // when
            // then
            testHelper.assertRegisterBoardFailedInvalidInput(resultFields, registerRequest, pictures, errorMessages);
        }

        @Test
        @DisplayName(FAIL_WRITER_NOT_FOUND)
        void boardRegisterMemberNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build();

            // when
            when(boardService.register(anyLong(), any(), any(), anyBoolean())).thenThrow(MemberNotFoundException.class);

            // then
            testHelper.assertRegisterBoardFailed(resultFields, registerRequest, pictures);
        }

        @Test
        @DisplayName(FAIL_CATEGORY_NOT_FOUND)
        void boardRegisterCategoryNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isBadRequest())
                    .status(400)
                    .result(false)
                    .message(CATEGORY_NOT_FOUND.getMessage())
                    .build();

            // when
            when(boardService.register(anyLong(), any(), any(), anyBoolean())).thenThrow(CategoryNotFoundException.class);

            // then
            testHelper.assertRegisterBoardFailed(resultFields, registerRequest, pictures);
        }

        @Test
        @DisplayName(SUCCESS_REGISTER_TMP_BOARD)
        void boardRegisterTmpSuccess() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isCreated())
                    .status(201)
                    .result(true)
                    .message(BOARD_REGISTER_TEMPORARY_SUCCESS.getMessage())
                    .build();

            // when
            when(boardService.register(anyLong(), any(), any(), anyBoolean())).thenReturn(1L);

            // then
            testHelper.assertRegisterTmpBoardSuccess(resultFields, registerRequest, pictures);
        }
    }

    @Nested
    @DisplayName(BOARD_DETAIL_CONTROLLER_TEST)
    class GetBoardDetail {

        @Test
        @DisplayName(SUCCESS)
        void boardDetailSuccess() throws Exception {
            // given
            Long boardId = 1L;
            BoardDetailResponseDto response = dtoFactory.createBoardDetailResponseDto();
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(BOARD_GET_DETAIL_SUCCESS.getMessage())
                    .build();

            // when
            when(boardService.getBoardDetail(anyLong(), any())).thenReturn(response);

            // then
            testHelper.assertGetBoardDetailSuccess(resultFields, "$.data.id", boardId.intValue());
        }

        @Test
        @DisplayName(FAIL_BOARD_NOT_FOUND)
        void boardDetailBoardNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isBadRequest())
                    .status(400)
                    .result(false)
                    .message(BOARD_NOT_FOUND.getMessage())
                    .build();

            // when
            when(boardService.getBoardDetail(anyLong(), any())).thenThrow(new BoardNotFoundException());

            // then
            testHelper.assertGetBoardDetailFailed(resultFields);
        }
    }

    @Nested
    @DisplayName(BOARD_SEARCH_CONTROLLER_TEST)
    class SearchBoards {

        @Test
        @DisplayName(SUCCESS)
        void searchBoardsSuccess() throws Exception {
            // given
            List<BoardSearchResponseDto> searchResponseDtos = dtoFactory.createBoardSearchResponseDtos(2);
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(SEARCH_BOARDS_SUCCESS.getMessage())
                    .build();

            // when
            PageResponseDto<BoardSearchResponseDto> response = dtoFactory.createBoardSearchResponse(0, 10, 2, searchResponseDtos);
            when(boardService.search(anyLong(), any(), any())).thenReturn(response);

            // then
            testHelper.assertSearchBoardsSuccess(resultFields, 2);
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void searchBoardsMemberNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(MemberNotFoundException.class).when(boardService).search(any(), any(), any());

            // then

            testHelper.assertSearchBoardsFailed(resultFields);
        }
    }

    @Nested
    @DisplayName(BOARD_MY_DETAIL_CONTROLLER_TEST)
    class SearchMyBoards {

        @Test
        @DisplayName(SUCCESS)
        void searchMyBoardsSuccess() throws Exception {
            // given
            PageResponseDto<BoardSearchResponseDto> response = dtoFactory.createBoardSearchResponse(0, 10, 30, dtoFactory.createBoardSearchResponseDtos(30));
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(SEARCH_BOARDS_SUCCESS.getMessage())
                    .build();

            // when
            when(boardService.searchMyBoards(anyLong(), any(), any())).thenReturn(response);

            // then
            testHelper.assertSearchMyBoardsSuccess(resultFields);
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void searchMyBoardsFailMemberNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(MemberNotFoundException.class).when(boardService).searchMyBoards(anyLong(), any(), any());

            // then
            testHelper.assertSearchMyBoardsFailed(resultFields);
        }
    }

    @Nested
    @DisplayName(BOARD_UPDATE_CONTROLLER_TEST)
    class UpdateBoard {

        MockMultipartFile updateRequest;
        MockMultipartFile[] newPictures;

        @BeforeEach
        public void setUp() throws Exception {
            this.updateRequest = dtoFactory.createUpdateRequest();
            this.newPictures = dtoFactory.createMockMultipartFiles("newPictures", 3);
        }

        @Test
        @DisplayName(SUCCESS)
        void boardUpdateSuccess() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(BOARD_UPDATE_SUCCESS.getMessage())
                    .build();

            // when
            doNothing().when(boardService).update(anyLong(), anyLong(), any(BoardUpdateRequestDto.class), any());

            // then
            testHelper.assertUpdateBoard(resultFields, newPictures, updateRequest);
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void boardUpdateFailMemberNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(MemberNotFoundException.class).when(boardService).update(anyLong(), anyLong(), any(BoardUpdateRequestDto.class), any());

            // then
            testHelper.assertUpdateBoard(resultFields, newPictures, updateRequest);
        }

        @Test
        @DisplayName(FAIL_BOARD_NOT_FOUND)
        void boardUpdateFailBoardNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isBadRequest())
                    .status(400)
                    .result(false)
                    .message(BOARD_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(BoardNotFoundException.class).when(boardService).update(anyLong(), anyLong(), any(BoardUpdateRequestDto.class), any());

            // then
            testHelper.assertUpdateBoard(resultFields, newPictures, updateRequest);
        }

        @Test
        @DisplayName(FAIL_MEMBER_IS_NOT_WRITER)
        void boardUpdateFailMemberIsNotWriter() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isForbidden())
                    .status(403)
                    .result(false)
                    .message(UNAUTHORIZED_ACCESS.getMessage())
                    .build();

            // when
            doThrow(UnauthorizedAccessException.class).when(boardService).update(anyLong(), anyLong(), any(BoardUpdateRequestDto.class), any());

            // then
            testHelper.assertUpdateBoard(resultFields, newPictures, updateRequest);
        }
    }

    @Nested
    @DisplayName(BOARD_DELETE_CONTROLLER_TEST)
    class DeleteBoard {

        @Test
        @DisplayName(SUCCESS)
        void deleteBoardSuccess() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(BOARD_DELETE_SUCCESS.getMessage())
                    .build();
            
            // when
            doNothing().when(boardService).delete(anyLong(), anyLong());

            // then
            testHelper.assertDeleteBoard(resultFields);
        }

        @Test
        @DisplayName(FAIL_BOARD_NOT_FOUND)
        void deleteBoardFailBoardNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isBadRequest())
                    .status(400)
                    .result(false)
                    .message(BOARD_NOT_FOUND.getMessage())
                    .build();
            
            // when
            doThrow(BoardNotFoundException.class).when(boardService).delete(anyLong(), anyLong());

            // then
            testHelper.assertDeleteBoard(resultFields);
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void deleteBoardFailMemberNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build();
            
            // when
            doThrow(MemberNotFoundException.class).when(boardService).delete(anyLong(), anyLong());

            // then
            testHelper.assertDeleteBoard(resultFields);
        }

        @Test
        @DisplayName(FAIL_MEMBER_IS_NOT_WRITER)
        void deleteBoardFailMemberIsNotWriter() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isForbidden())
                    .status(403)
                    .result(false)
                    .message(UNAUTHORIZED_ACCESS.getMessage())
                    .build();

            // when
            doThrow(UnauthorizedAccessException.class).when(boardService).delete(anyLong(), anyLong());

            // then

            testHelper.assertDeleteBoard(resultFields);
        }
    }

    @Nested
    @DisplayName(BOARD_GET_TMP_CONTROLLER_TEST)
    class GetTmpBoardDetail {

        @Test
        @DisplayName(SUCCESS)
        void getTmpBoardDetailSuccess() throws Exception {
            // given
            BoardDetailResponseDto response = dtoFactory.createBoardDetailResponseDto();
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(BOARD_GET_TMP_SUCCESS.getMessage())
                    .build();

            // when
            when(boardService.getTmpBoardDetail(anyLong())).thenReturn(response);

            // then
            testHelper.assertGetTmpBoardDetailSuccess(resultFields, "$.data.id", response.getId().intValue());
        }

        @Test
        @DisplayName(SUCCESS_NO_TMP_BOARDS)
        void getTmpBoardDetailSuccessNotTmpBoard() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(BOARD_GET_TMP_SUCCESS.getMessage())
                    .build();

            // when
            when(boardService.getTmpBoardDetail(anyLong())).thenReturn(null);

            // then
            testHelper.assertTmpBoardDetailFailed(resultFields);
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void getTmpBoardDetailFailMemberNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(MemberNotFoundException.class).when(boardService).getTmpBoardDetail(anyLong());

            // then
            testHelper.assertTmpBoardDetailFailed(resultFields);
        }
    }
}