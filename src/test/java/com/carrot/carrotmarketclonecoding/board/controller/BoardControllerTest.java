package com.carrot.carrotmarketclonecoding.board.controller;

import static com.carrot.carrotmarketclonecoding.board.displayname.BoardTestDisplayNames.MESSAGE.*;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.*;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.*;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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
import com.carrot.carrotmarketclonecoding.board.domain.enums.SearchOrder;
import com.carrot.carrotmarketclonecoding.board.domain.enums.Status;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardSearchRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.MyBoardSearchRequestDto;
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
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@WithCustomMockUser
@WebMvcTest(controllers = BoardController.class)
class BoardControllerTest {

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
            mvc.perform(post("/board/register")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .param("title", "title")
                            .param("categoryId", "1")
                            .param("method", "SELL")
                            .param("price", "200000")
                            .param("suggest", "true")
                            .param("description", "description")
                            .param("place", "place"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status", equalTo(201)))
                    .andExpect(jsonPath("$.result", equalTo(true)))
                    .andExpect(jsonPath("$.message", equalTo(BOARD_REGISTER_SUCCESS.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName(SUCCESS_REGISTER_TMP_BOARD)
        void boardRegisterTmpSuccess() throws Exception {
            // given
            // when
            when(boardService.register(any(), anyLong(), anyBoolean())).thenReturn(1L);

            // then
            mvc.perform(post("/board/register/tmp")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .param("title", "title")
                            .param("categoryId", "1")
                            .param("method", "SELL")
                            .param("price", "200000")
                            .param("suggest", "true")
                            .param("description", "description")
                            .param("place", "place"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status", equalTo(201)))
                    .andExpect(jsonPath("$.result", equalTo(true)))
                    .andExpect(jsonPath("$.message", equalTo(BOARD_REGISTER_TEMPORARY_SUCCESS.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName(FAIL_FILE_COUNT_OVER_10)
        void fileUploadLimitExceeded() throws Exception {
            // given
            MockMultipartFile[] pictures = new MockMultipartFile[11];
            for (int i = 0; i <= 10; i++) {
                pictures[i] = new MockMultipartFile(
                        "pictures",
                        "picture " + i + ".png",
                        "image/png",
                        ("picture " + i).getBytes());
            }

            // when
            when(boardService.register(any(), anyLong(), anyBoolean())).thenThrow(FileUploadLimitException.class);

            // then
            mvc.perform(multipart("/board/register")
                            .file(pictures[0])
                            .file(pictures[1])
                            .file(pictures[2])
                            .file(pictures[3])
                            .file(pictures[4])
                            .file(pictures[5])
                            .file(pictures[6])
                            .file(pictures[7])
                            .file(pictures[8])
                            .file(pictures[9])
                            .file(pictures[10])
                            .param("title", "title")
                            .param("categoryId", "1")
                            .param("method", "SELL")
                            .param("price", "200000")
                            .param("suggest", "true")
                            .param("description", "description")
                            .param("place", "place")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.status", equalTo(500)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(FILE_UPLOAD_LIMIT.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName(FAIL_INPUT_NOT_VALID)
        void boardRegisterValidationFailed() throws Exception {
            // given
            Map<String, String> map = new HashMap<>();
            map.put("title", MESSAGE.TITLE_NOT_VALID);

            // when
            // then
            mvc.perform(post("/board/register")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .param("title", "")
                            .param("categoryId", "1")
                            .param("method", "SELL")
                            .param("price", "200000")
                            .param("suggest", "true")
                            .param("description", "description")
                            .param("place", "place"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(INPUT_NOT_VALID.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(map)));
        }

        @Test
        @DisplayName(FAIL_WRITER_NOT_FOUND)
        void boardRegisterMemberNotFound() throws Exception {
            // given
            // when
            when(boardService.register(any(), anyLong(), anyBoolean())).thenThrow(MemberNotFoundException.class);

            // then
            mvc.perform(post("/board/register")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .param("title", "title")
                            .param("categoryId", "1")
                            .param("method", "SELL")
                            .param("price", "200000")
                            .param("suggest", "true")
                            .param("description", "description")
                            .param("place", "place"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status", equalTo(401)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(MEMBER_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName(FAIL_CATEGORY_NOT_FOUND)
        void boardRegisterCategoryNotFound() throws Exception {
            // given
            // when
            when(boardService.register(any(), anyLong(), anyBoolean())).thenThrow(CategoryNotFoundException.class);

            // then
            mvc.perform(post("/board/register")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .param("title", "title")
                            .param("categoryId", "1")
                            .param("method", "SELL")
                            .param("price", "200000")
                            .param("suggest", "true")
                            .param("description", "description")
                            .param("place", "place"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(CATEGORY_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }
    }

    @Nested
    @DisplayName(BOARD_DETAIL_CONTROLLER_TEST)
    class BoardDetail {

        @Test
        @DisplayName(SUCCESS)
        void boardDetailSuccess() throws Exception {
            // given
            Long boardId = 1L;
            BoardDetailResponseDto response = BoardDetailResponseDto.builder()
                    .id(boardId)
                    .build();

            // when
            when(boardService.detail(anyLong(), anyString())).thenReturn(response);

            // then
            mvc.perform(get("/board/{id}", boardId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", equalTo(200)))
                    .andExpect(jsonPath("$.result", equalTo(true)))
                    .andExpect(jsonPath("$.message", equalTo(BOARD_GET_DETAIL_SUCCESS.getMessage())))
                    .andExpect(jsonPath("$.data.id", equalTo(boardId.intValue())));
        }

        @Test
        @DisplayName(FAIL_BOARD_NOT_FOUND)
        void boardDetailBoardNotFound() throws Exception {
            // given

            // when
            when(boardService.detail(anyLong(), anyString())).thenThrow(new BoardNotFoundException());

            // then
            mvc.perform(get("/board/{id}", 1L))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(BOARD_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }
    }

    @Nested
    @DisplayName(BOARD_SEARCH_CONTROLLER_TEST)
    class SearchBoard {

        @Test
        @DisplayName(SUCCESS)
        void searchBoardsSuccess() throws Exception {
            // given
            PageResponseDto<BoardSearchResponseDto> response = createBoardSearchResponse(2, 0, 10, 2);
            BoardSearchRequestDto searchRequestDto = BoardSearchRequestDto
                    .builder()
                    .categoryId(1L)
                    .keyword("title")
                    .minPrice(0)
                    .maxPrice(20000)
                    .order(SearchOrder.NEWEST)
                    .build();

            // when
            when(boardService.search(anyLong(), any(), any())).thenReturn(response);

            // then
            mvc.perform(get("/board")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(searchRequestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", equalTo(200)))
                    .andExpect(jsonPath("$.result", equalTo(true)))
                    .andExpect(jsonPath("$.message", equalTo(SEARCH_BOARDS_SUCCESS.getMessage())))
                    .andExpect(jsonPath("$.data.contents.size()", equalTo(2)));
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void searchBoardsMemberNotFound() throws Exception {
            // given
            BoardSearchRequestDto searchRequestDto = BoardSearchRequestDto
                    .builder()
                    .categoryId(1L)
                    .keyword("title")
                    .minPrice(0)
                    .maxPrice(20000)
                    .order(SearchOrder.NEWEST)
                    .build();

            // when
            doThrow(MemberNotFoundException.class).when(boardService).search(any(), any(), any());

            // then
            mvc.perform(get("/board")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(searchRequestDto)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status", equalTo(401)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(MEMBER_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }
    }

    @Nested
    @DisplayName(BOARD_MY_DETAIL_CONTROLLER_TEST)
    class SearchBoardByStatus {

        @Test
        @DisplayName(SUCCESS)
        void searchBoardsByStatusSuccess() throws Exception {
            // given
            PageResponseDto<BoardSearchResponseDto> response = createBoardSearchResponse(10, 0, 10, 30);
            MyBoardSearchRequestDto searchRequestDto = new MyBoardSearchRequestDto(Status.SELL, true);

            // when
            when(boardService.searchMyBoards(anyLong(), any(), any())).thenReturn(response);

            // then
            mvc.perform(get("/board/my")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(searchRequestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", equalTo(200)))
                    .andExpect(jsonPath("$.result", equalTo(true)))
                    .andExpect(jsonPath("$.message", equalTo(SEARCH_BOARDS_SUCCESS.getMessage())))
                    .andExpect(jsonPath("$.data.contents.size()", equalTo(10)))
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
            mvc.perform(get("/board/my")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(new MyBoardSearchRequestDto(Status.SELL, true))))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status", equalTo(401)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(MEMBER_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
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
            mvc.perform(patch("/board/{id}", 1L)
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .param("title", "title2")
                            .param("categoryId", "2")
                            .param("method", "SHARE")
                            .param("price", "200000")
                            .param("suggest", "true")
                            .param("description", "description2")
                            .param("place", "place2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", equalTo(200)))
                    .andExpect(jsonPath("$.result", equalTo(true)))
                    .andExpect(jsonPath("$.message", equalTo(BOARD_UPDATE_SUCCESS.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void boardUpdateFailMemberNotFound() throws Exception {
            // given
            // when
            doThrow(MemberNotFoundException.class).when(boardService).update(any(), anyLong(), anyLong());

            // then
            mvc.perform(patch("/board/{id}", 1L)
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .param("title", "title2")
                            .param("categoryId", "2")
                            .param("method", "SHARE")
                            .param("price", "200000")
                            .param("suggest", "true")
                            .param("description", "description2")
                            .param("place", "place2"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status", equalTo(401)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(MEMBER_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName(FAIL_BOARD_NOT_FOUND)
        void boardUpdateFailBoardNotFound() throws Exception {
            // given
            // when
            doThrow(BoardNotFoundException.class).when(boardService).update(any(), anyLong(), anyLong());

            // then
            mvc.perform(patch("/board/{id}", 1L)
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .param("title", "title2")
                            .param("categoryId", "2")
                            .param("method", "SHARE")
                            .param("price", "200000")
                            .param("suggest", "true")
                            .param("description", "description2")
                            .param("place", "place2"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(BOARD_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName(FAIL_MEMBER_IS_NOT_WRITER)
        void boardUpdateFailMemberIsNotWriter() throws Exception {
            // given
            // when
            doThrow(UnauthorizedAccessException.class).when(boardService).update(any(), anyLong(), anyLong());

            // then
            mvc.perform(patch("/board/{id}", 1L)
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .param("title", "title2")
                            .param("categoryId", "2")
                            .param("method", "SHARE")
                            .param("price", "200000")
                            .param("suggest", "true")
                            .param("description", "description2")
                            .param("place", "place2"))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.status", equalTo(403)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(UNAUTHORIZED_ACCESS.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }
    }

    @Nested
    @DisplayName(BOARD_DELETE_CONTROLLER_TEST)
    class DeleteBoard {

        @Test
        @DisplayName(SUCCESS)
        void deleteBoardSuccess() throws Exception {
            // given
            Long boardId = 1L;

            // when
            doNothing().when(boardService).delete(anyLong(), anyLong());

            // then
            mvc.perform(delete("/board/{id}", boardId)
                            .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", equalTo(200)))
                    .andExpect(jsonPath("$.result", equalTo(true)))
                    .andExpect(jsonPath("$.message", equalTo(BOARD_DELETE_SUCCESS.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName(FAIL_BOARD_NOT_FOUND)
        void deleteBoardFailBoardNotFound() throws Exception {
            // given
            Long boardId = 1L;

            // when
            doThrow(BoardNotFoundException.class).when(boardService).delete(anyLong(), anyLong());

            // then
            mvc.perform(delete("/board/{id}", boardId)
                            .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(BOARD_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void deleteBoardFailMemberNotFound() throws Exception {
            // given
            // when
            doThrow(MemberNotFoundException.class).when(boardService).delete(anyLong(), anyLong());

            // then
            mvc.perform(delete("/board/{id}", 1L)
                            .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status", equalTo(401)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(MEMBER_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName(FAIL_MEMBER_IS_NOT_WRITER)
        void deleteBoardFailMemberIsNotWriter() throws Exception {
            // given
            // when
            doThrow(UnauthorizedAccessException.class).when(boardService).delete(anyLong(), anyLong());

            // then
            mvc.perform(delete("/board/{id}", 1L)
                            .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.status", equalTo(403)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(UNAUTHORIZED_ACCESS.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
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
            mvc.perform(get("/board/tmp"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", equalTo(200)))
                    .andExpect(jsonPath("$.result", equalTo(true)))
                    .andExpect(jsonPath("$.message", equalTo(BOARD_GET_TMP_SUCCESS.getMessage())))
                    .andExpect(jsonPath("$.data.id", equalTo(response.getId().intValue())));
        }

        @Test
        @DisplayName(SUCCESS_NO_TMP_BOARDS)
        void boardDetailSuccessNotTmpBoard() throws Exception {
            // given
            // when
            when(boardService.tmpBoardDetail(anyLong())).thenReturn(null);

            // then
            mvc.perform(get("/board/tmp"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", equalTo(200)))
                    .andExpect(jsonPath("$.result", equalTo(true)))
                    .andExpect(jsonPath("$.message", equalTo(BOARD_GET_TMP_SUCCESS.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void boardDetailFailMemberNotFound() throws Exception {
            // given
            // when
            doThrow(MemberNotFoundException.class).when(boardService).tmpBoardDetail(anyLong());

            // then
            mvc.perform(get("/board/tmp")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status", equalTo(401)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(MEMBER_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
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