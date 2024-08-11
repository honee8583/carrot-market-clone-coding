package com.carrot.carrotmarketclonecoding.board.controller;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.*;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.*;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardDetailResponseDto;
import com.carrot.carrotmarketclonecoding.board.dto.validation.BoardRegisterValidationMessage.MESSAGE;
import com.carrot.carrotmarketclonecoding.board.service.impl.BoardServiceImpl;
import com.carrot.carrotmarketclonecoding.common.exception.BoardNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.CategoryNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.FileUploadLimitException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BoardController.class)
class BoardControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    BoardServiceImpl boardService;

    @Nested
    @DisplayName("게시글 작성 컨트롤러 테스트")
    class BoardRegisterControllerTest {

        @Test
        @DisplayName("성공")
        void boardRegisterSuccess() throws Exception {
            // given
            // when
            when(boardService.register(any(), anyLong())).thenReturn(1L);

            // then
            mvc.perform(multipart("/board/register")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                            .param("title", "title")
                            .param("categoryId", "1")
                            .param("method", "SELL")
                            .param("price", "200000")
                            .param("suggest", "true")
                            .param("description", "description")
                            .param("place", "place")
                            .param("tmp", "false"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status", equalTo(201)))
                    .andExpect(jsonPath("$.result", equalTo(true)))
                    .andExpect(jsonPath("$.message", equalTo(BOARD_REGISTER_SUCCESS.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName("실패 - 업로드 요청한 파일의 개수가 10개 초과")
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
            when(boardService.register(any(), anyLong())).thenThrow(FileUploadLimitException.class);

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
                            .param("tmp", "false")
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.status", equalTo(500)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(FILE_UPLOAD_LIMIT.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName("실패 - 유효성 검사 실패")
        void boardRegisterValidationFailed() throws Exception {
            // given
            Map<String, String> map = new HashMap<>();
            map.put("title", MESSAGE.TITLE_NOT_VALID);

            // when
            // then
            mvc.perform(multipart("/board/register")
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .param("title", "")
                            .param("categoryId", "1")
                            .param("method", "SELL")
                            .param("price", "200000")
                            .param("suggest", "true")
                            .param("description", "description")
                            .param("place", "place")
                            .param("tmp", "false"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(INPUT_NOT_VALID.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(map)));
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 작성자")
        void boardRegisterMemberNotFound() throws Exception {
            // given
            // when
            when(boardService.register(any(), anyLong())).thenThrow(MemberNotFoundException.class);

            // then
            mvc.perform(post("/board/register")
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .param("title", "title")
                            .param("categoryId", "1")
                            .param("method", "SELL")
                            .param("price", "200000")
                            .param("suggest", "true")
                            .param("description", "description")
                            .param("place", "place")
                            .param("tmp", "false"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status", equalTo(401)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(MEMBER_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 카테고리")
        void boardRegisterCategoryNotFound() throws Exception {
            // given
            // when
            when(boardService.register(any(), anyLong())).thenThrow(CategoryNotFoundException.class);

            // then
            mvc.perform(post("/board/register")
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .param("title", "title")
                            .param("categoryId", "1")
                            .param("method", "SELL")
                            .param("price", "200000")
                            .param("suggest", "true")
                            .param("description", "description")
                            .param("place", "place")
                            .param("tmp", "false"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(CATEGORY_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }
    }

    @Nested
    @DisplayName("게시글 조회 컨트롤러 테스트")
    class BoardDetail {

        @Test
        @DisplayName("성공")
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
        @DisplayName("실패 - 존재하지 않는 게시판 아이디")
        void boardDetailBoardNotFound() throws Exception {
            // given
            Long boardId = 1L;

            // when
            when(boardService.detail(anyLong(), anyString())).thenThrow(new BoardNotFoundException());

            // then
            mvc.perform(get("/board/{id}", boardId))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(BOARD_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }
    }
}