package com.carrot.carrotmarketclonecoding.board.controller;

import static com.carrot.carrotmarketclonecoding.board.displayname.BoardTestDisplayNames.MESSAGE.*;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.*;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.*;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrot.carrotmarketclonecoding.auth.config.WithCustomMockUser;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardDetailResponseDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardSearchResponseDto;
import com.carrot.carrotmarketclonecoding.board.dto.validation.BoardRegisterValidationMessage.MESSAGE;
import com.carrot.carrotmarketclonecoding.board.service.impl.BoardServiceImpl;
import com.carrot.carrotmarketclonecoding.common.exception.BoardNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.CategoryNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.FileUploadLimitException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.UnauthorizedAccessException;
import com.carrot.carrotmarketclonecoding.common.response.PageResponseDto;
import com.carrot.carrotmarketclonecoding.util.ControllerTestUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

@WithCustomMockUser
@WebMvcTest(controllers = BoardController.class)
class BoardControllerTest extends ControllerTestUtil {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BoardServiceImpl boardService;

    @Nested
    @DisplayName(BOARD_REGISTER_CONTROLLER_TEST)
    class BoardRegisterControllerTest {

        @Test
        @DisplayName(SUCCESS)
        void boardRegisterSuccess() throws Exception {
            // given
            // when
            when(boardService.register(any(), any(), anyBoolean())).thenReturn(1L);

            // then
            assertResult(mvc.perform(requestWithCsrf(post("/board/register"), MediaType.MULTIPART_FORM_DATA)
                            .param("title", "title")
                            .param("categoryId", "1")
                            .param("method", "SELL")
                            .param("price", "200000")
                            .param("suggest", "true")
                            .param("description", "description")
                            .param("place", "place")),
                    status().isCreated(), 201, true,
                    BOARD_REGISTER_SUCCESS.getMessage(),
                    "$.data", null);
        }

        @Test
        @DisplayName(SUCCESS_REGISTER_TMP_BOARD)
        void boardRegisterTmpSuccess() throws Exception {
            // given
            // when
            when(boardService.register(any(), anyLong(), anyBoolean())).thenReturn(1L);

            // then
            assertResult(mvc.perform(requestWithCsrf(post("/board/register/tmp"), MediaType.MULTIPART_FORM_DATA)
                            .param("title", "title")
                            .param("categoryId", "1")
                            .param("method", "SELL")
                            .param("price", "200000")
                            .param("suggest", "true")
                            .param("description", "description")
                            .param("place", "place")),
                    status().isCreated(), 201, true,
                    BOARD_REGISTER_TEMPORARY_SUCCESS.getMessage(),
                    "$.data", null);
        }

        @Test
        @DisplayName(FAIL_FILE_COUNT_OVER_10)
        void fileUploadLimitExceeded() throws Exception {
            // given
            // when
            when(boardService.register(any(), anyLong(), anyBoolean())).thenThrow(FileUploadLimitException.class);

            // then
            MockMultipartFile[] pictures = createMockMultipartFiles(11);
            assertResult(mvc.perform(requestWithCsrf(
                            requestMultipartFiles(multipart("/board/register"), pictures)
                            , MediaType.MULTIPART_FORM_DATA)
                            .param("title", "title")
                            .param("categoryId", "1")
                            .param("method", "SELL")
                            .param("price", "200000")
                            .param("suggest", "true")
                            .param("description", "description")
                            .param("place", "place")),
                    status().isInternalServerError(), 500, false,
                    FILE_UPLOAD_LIMIT.getMessage(),
                    "$.data", null);
        }

        private MockMultipartHttpServletRequestBuilder requestMultipartFiles(MockMultipartHttpServletRequestBuilder requestBuilder,
                                                                             MockMultipartFile[] pictures) {
            for (MockMultipartFile picture : pictures) {
                requestBuilder.file(picture);
            }
            return requestBuilder;
        }

        private MockMultipartFile[] createMockMultipartFiles(int size) {
            MockMultipartFile[] pictures = new MockMultipartFile[size];
            for (int i = 0; i < size; i++) {
                pictures[i] = new MockMultipartFile(
                        "pictures",
                        "picture " + i + ".png",
                        "image/png",
                        ("picture " + i).getBytes());
            };
            return pictures;
        }

        @Test
        @DisplayName(FAIL_INPUT_NOT_VALID)
        void boardRegisterValidationFailed() throws Exception {
            // given
            Map<String, String> map = new HashMap<>();
            map.put("title", MESSAGE.TITLE_NOT_VALID);

            // when
            // then
            assertResult(mvc.perform(requestWithCsrf(post("/board/register"), MediaType.MULTIPART_FORM_DATA)
                            .param("title", "")
                            .param("categoryId", "1")
                            .param("method", "SELL")
                            .param("price", "200000")
                            .param("suggest", "true")
                            .param("description", "description")
                            .param("place", "place")),
                    status().isBadRequest(), 400, false,
                    INPUT_NOT_VALID.getMessage(),
                    "$.data", map);
        }

        @Test
        @DisplayName(FAIL_WRITER_NOT_FOUND)
        void boardRegisterMemberNotFound() throws Exception {
            // given
            // when
            when(boardService.register(any(), anyLong(), anyBoolean())).thenThrow(MemberNotFoundException.class);

            // then
            assertResult(mvc.perform(requestWithCsrf(post("/board/register"), MediaType.MULTIPART_FORM_DATA)
                            .param("title", "title")
                            .param("categoryId", "1")
                            .param("method", "SELL")
                            .param("price", "200000")
                            .param("suggest", "true")
                            .param("description", "description")
                            .param("place", "place")),
                    status().isUnauthorized(), 401, false,
                    MEMBER_NOT_FOUND.getMessage(),
                    "$.data", null);
        }

        @Test
        @DisplayName(FAIL_CATEGORY_NOT_FOUND)
        void boardRegisterCategoryNotFound() throws Exception {
            // given
            // when
            when(boardService.register(any(), anyLong(), anyBoolean())).thenThrow(CategoryNotFoundException.class);

            // then
            assertResult(mvc.perform(requestWithCsrf(post("/board/register")
                            .param("title", "title")
                            .param("categoryId", "1")
                            .param("method", "SELL")
                            .param("price", "200000")
                            .param("suggest", "true")
                            .param("description", "description")
                            .param("place", "place"), MediaType.MULTIPART_FORM_DATA)),
                    status().isBadRequest(), 400, false,
                    CATEGORY_NOT_FOUND.getMessage(),
                    "$.data",null);
        }
    }

    @Nested
    @DisplayName(BOARD_DETAIL_CONTROLLER_TEST)
    class BoardDetail {

        @Test
        @DisplayName(SUCCESS)
        void boardDetailSuccess() throws Exception {
            // given
            // when
            Long boardId = 1L;
            BoardDetailResponseDto response = BoardDetailResponseDto.builder()
                    .id(boardId)
                    .build();
            when(boardService.detail(anyLong(), any())).thenReturn(response);

            // then
            assertResult(mvc.perform(get("/board/{id}", boardId)),
                    status().isOk(), 200, true,
                    BOARD_GET_DETAIL_SUCCESS.getMessage(),
                    "$.data.id", boardId.intValue());
        }

        @Test
        @DisplayName(FAIL_BOARD_NOT_FOUND)
        void boardDetailBoardNotFound() throws Exception {
            // given
            // when
            when(boardService.detail(anyLong(), any())).thenThrow(new BoardNotFoundException());

            // then
            assertResult(mvc.perform(get("/board/{id}", 1L)),
                    status().isBadRequest(), 400, false,
                    BOARD_NOT_FOUND.getMessage(),
                    "$.data", null);
        }
    }

    @Nested
    @DisplayName(BOARD_SEARCH_CONTROLLER_TEST)
    class SearchBoard {

        @Test
        @DisplayName(SUCCESS)
        void searchBoardsSuccess() throws Exception {
            // given
            // when
            PageResponseDto<BoardSearchResponseDto> response = createBoardSearchResponse(2, 0, 10, 2);
            when(boardService.search(anyLong(), any(), any())).thenReturn(response);

            // then
            assertResult(mvc.perform(get("/board")
                            .param("categoryId", "1")
                            .param("keyword", "title")
                            .param("minPrice", "0")
                            .param("maxPrice", "20000")
                            .param("order", "NEWEST")),
                    status().isOk(), 200, true,
                    SEARCH_BOARDS_SUCCESS.getMessage(),
                    "$.data.contents.size()", 2);
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void searchBoardsMemberNotFound() throws Exception {
            // given
            // when
            doThrow(MemberNotFoundException.class).when(boardService).search(any(), any(), any());

            // then
            assertResult(mvc.perform(get("/board")
                            .param("categoryId", "1")
                            .param("keyword", "title")
                            .param("minPrice", "0")
                            .param("maxPrice", "20000")
                            .param("order", "NEWEST")),
                    status().isUnauthorized(), 401, false,
                    MEMBER_NOT_FOUND.getMessage(),
                    "$.data", null);
        }
    }

    @Nested
    @DisplayName(BOARD_MY_DETAIL_CONTROLLER_TEST)
    class SearchBoardByStatus {

        @Test
        @DisplayName(SUCCESS)
        void searchBoardsByStatusSuccess() throws Exception {
            // given
            // when
            PageResponseDto<BoardSearchResponseDto> response = createBoardSearchResponse(10, 0, 10, 30);
            when(boardService.searchMyBoards(anyLong(), any(), any())).thenReturn(response);

            // then
            assertResult(mvc.perform(get("/board/my")
                            .param("status", "SELL")
                            .param("hide", "true")),
                    status().isOk(), 200, true,
                    SEARCH_BOARDS_SUCCESS.getMessage(),
                    "$.data.contents.size()", 10)
                    .andExpect(jsonPath("$.data.totalPage", equalTo(3)))
                    .andExpect(jsonPath("$.data.totalElements", equalTo(30)))
                    .andExpect(jsonPath("$.data.first", equalTo(true)))
                    .andExpect(jsonPath("$.data.last", equalTo(false)))
                    .andExpect(jsonPath("$.data.numberOfElements", equalTo(10)));
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void searchBoardsByStatusFailMemberNotFound() throws Exception {
            // given
            // when
            doThrow(MemberNotFoundException.class).when(boardService).searchMyBoards(anyLong(), any(), any());

            // then
            assertResult(mvc.perform(get("/board/my")
                            .param("status", "SELL")
                            .param("hide", "true")),
                    status().isUnauthorized(), 401, false,
                    MEMBER_NOT_FOUND.getMessage(),
                    "$.data", null);
        }
    }

    @Nested
    @DisplayName(BOARD_UPDATE_CONTROLLER_TEST)
    class UpdateBoard {

        @Test
        @DisplayName(SUCCESS)
        void boardUpdateSuccess() throws Exception {
            // given
            // when
            doNothing().when(boardService).update(any(), anyLong(), anyLong());

            // then
            assertResult(mvc.perform(requestWithCsrf(patch("/board/{id}", 1L), MediaType.MULTIPART_FORM_DATA)
                            .param("title", "title2")
                            .param("categoryId", "2")
                            .param("method", "SHARE")
                            .param("price", "200000")
                            .param("suggest", "true")
                            .param("description", "description2")
                            .param("place", "place2")),
                    status().isOk(), 200, true,
                    BOARD_UPDATE_SUCCESS.getMessage(),
                    "$.data", null);
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void boardUpdateFailMemberNotFound() throws Exception {
            // given
            // when
            doThrow(MemberNotFoundException.class).when(boardService).update(any(), anyLong(), anyLong());

            // then
            assertResult(mvc.perform(requestWithCsrf(patch("/board/{id}", 1L), MediaType.MULTIPART_FORM_DATA)
                            .param("title", "title2")
                            .param("categoryId", "2")
                            .param("method", "SHARE")
                            .param("price", "200000")
                            .param("suggest", "true")
                            .param("description", "description2")
                            .param("place", "place2")),
                    status().isUnauthorized(), 401, false,
                    MEMBER_NOT_FOUND.getMessage(),
                    "$.data", null);
        }

        @Test
        @DisplayName(FAIL_BOARD_NOT_FOUND)
        void boardUpdateFailBoardNotFound() throws Exception {
            // given
            // when
            doThrow(BoardNotFoundException.class).when(boardService).update(any(), anyLong(), anyLong());

            // then
            assertResult(mvc.perform(requestWithCsrf(patch("/board/{id}", 1L), MediaType.MULTIPART_FORM_DATA)
                            .param("title", "title2")
                            .param("categoryId", "2")
                            .param("method", "SHARE")
                            .param("price", "200000")
                            .param("suggest", "true")
                            .param("description", "description2")
                            .param("place", "place2")),
                    status().isBadRequest(), 400, false,
                    BOARD_NOT_FOUND.getMessage(),
                    "$.data", null);
        }

        @Test
        @DisplayName(FAIL_MEMBER_IS_NOT_WRITER)
        void boardUpdateFailMemberIsNotWriter() throws Exception {
            // given
            // when
            doThrow(UnauthorizedAccessException.class).when(boardService).update(any(), anyLong(), anyLong());

            // then
            assertResult(mvc.perform(patch("/board/{id}", 1L)
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .param("title", "title2")
                            .param("categoryId", "2")
                            .param("method", "SHARE")
                            .param("price", "200000")
                            .param("suggest", "true")
                            .param("description", "description2")
                            .param("place", "place2")),
                    status().isForbidden(), 403, false,
                    UNAUTHORIZED_ACCESS.getMessage(),
                    "$.data", null);
        }
    }

    @Nested
    @DisplayName(BOARD_DELETE_CONTROLLER_TEST)
    class DeleteBoard {

        @Test
        @DisplayName(SUCCESS)
        void deleteBoardSuccess() throws Exception {
            // given
            // when
            doNothing().when(boardService).delete(anyLong(), anyLong());

            // then
            assertResult(mvc.perform(requestWithCsrf(delete("/board/{id}", 1L), null)),
                    status().isOk(), 200, true,
                    BOARD_DELETE_SUCCESS.getMessage(),
                    "$.data", null);
        }

        @Test
        @DisplayName(FAIL_BOARD_NOT_FOUND)
        void deleteBoardFailBoardNotFound() throws Exception {
            // given
            // when
            doThrow(BoardNotFoundException.class).when(boardService).delete(anyLong(), anyLong());

            // then
            assertResult(mvc.perform(requestWithCsrf(delete("/board/{id}", 1L), null)),
                    status().isBadRequest(), 400, false,
                    BOARD_NOT_FOUND.getMessage(),
                    "$.data", null);
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void deleteBoardFailMemberNotFound() throws Exception {
            // given
            // when
            doThrow(MemberNotFoundException.class).when(boardService).delete(anyLong(), anyLong());

            // then
            assertResult(mvc.perform(requestWithCsrf(delete("/board/{id}", 1L), null)),
                    status().isUnauthorized(), 401, false,
                    MEMBER_NOT_FOUND.getMessage(),
                    "$.data", null);
        }

        @Test
        @DisplayName(FAIL_MEMBER_IS_NOT_WRITER)
        void deleteBoardFailMemberIsNotWriter() throws Exception {
            // given
            // when
            doThrow(UnauthorizedAccessException.class).when(boardService).delete(anyLong(), anyLong());

            // then
            assertResult(mvc.perform(requestWithCsrf(delete("/board/{id}", 1L), null)),
                    status().isForbidden(), 403, false,
                    UNAUTHORIZED_ACCESS.getMessage(),
                    "$.data", null);
        }
    }

    @Nested
    @DisplayName(BOARD_GET_TMP_CONTROLLER_TEST)
    class TmpBoardDetail {

        @Test
        @DisplayName(SUCCESS)
        void boardDetailSuccess() throws Exception {
            // given
            BoardDetailResponseDto response = BoardDetailResponseDto.builder()
                    .id(1L)
                    .build();

            // when
            when(boardService.tmpBoardDetail(anyLong())).thenReturn(response);

            // then
            assertResult(mvc.perform(get("/board/tmp")),
                    status().isOk(), 200, true,
                    BOARD_GET_TMP_SUCCESS.getMessage(),
                    "$.data.id", response.getId().intValue());
        }

        @Test
        @DisplayName(SUCCESS_NO_TMP_BOARDS)
        void boardDetailSuccessNotTmpBoard() throws Exception {
            // given
            // when
            when(boardService.tmpBoardDetail(anyLong())).thenReturn(null);

            // then
            assertResult(mvc.perform(get("/board/tmp")),
                    status().isOk(), 200, true,
                    BOARD_GET_TMP_SUCCESS.getMessage(),
                    "$.data", null);
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void boardDetailFailMemberNotFound() throws Exception {
            // given
            // when
            doThrow(MemberNotFoundException.class).when(boardService).tmpBoardDetail(anyLong());

            // then
            assertResult(mvc.perform(get("/board/tmp")),
                    status().isUnauthorized(), 401, false,
                    MEMBER_NOT_FOUND.getMessage(),
                    "$.data", null);
        }
    }

    private PageResponseDto<BoardSearchResponseDto> createBoardSearchResponse(int length, int page, int size, int total) {
        List<BoardSearchResponseDto> boardSearchResponse = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            boardSearchResponse.add(new BoardSearchResponseDto());
        }
        return new PageResponseDto<>(new PageImpl<>(boardSearchResponse, PageRequest.of(page, size), total));
    }
}