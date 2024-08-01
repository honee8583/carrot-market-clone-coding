package com.carrot.carrotmarketclonecoding.board.controller;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.CATEGORY_NOT_FOUND;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.MEMBER_NOT_FOUND;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.BOARD_REGISTER_SUCCESS;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrot.carrotmarketclonecoding.board.domain.enums.Method;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.validation.BoardRegisterValidationMessage.MESSAGE;
import com.carrot.carrotmarketclonecoding.board.service.impl.BoardServiceImpl;
import com.carrot.carrotmarketclonecoding.common.exception.CategoryNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.response.FailedMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
        void boardRegister() throws Exception {
            // given
            BoardRegisterRequestDto requestDto = BoardRegisterRequestDto.builder()
                    .pictures(null)
                    .title("title")
                    .categoryId(1L)
                    .method(Method.SELL)
                    .price(200000)
                    .suggest(true)
                    .description("description")
                    .place("place")
                    .tmp(false)
                    .build();

            // when
            // then
            mvc.perform(post("/board/register")
                    .content(new ObjectMapper().writeValueAsString(requestDto))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status", equalTo(201)))
                    .andExpect(jsonPath("$.result", equalTo(true)))
                    .andExpect(jsonPath("$.message", equalTo(BOARD_REGISTER_SUCCESS.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));

        }

        @Test
        @DisplayName("실패 - 유효성 검사 실패")
        void boardRegisterValidationFailed() throws Exception {
            // given
            BoardRegisterRequestDto requestDto = BoardRegisterRequestDto.builder()
                    .pictures(null)
                    .title("")
                    .categoryId(1L)
                    .method(Method.SELL)
                    .price(200000)
                    .suggest(true)
                    .description("description")
                    .place("place")
                    .tmp(false)
                    .build();

            Map<String, String> map = new HashMap<>();
            map.put("title", MESSAGE.TITLE_NOT_VALID);

            // when
            // then
            mvc.perform(post("/board/register")
                    .content(new ObjectMapper().writeValueAsString(requestDto))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(FailedMessage.INPUT_NOT_VALID.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(map)));
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 작성자")
        void boardRegisterMemberNotFound() throws Exception {
            // given
            BoardRegisterRequestDto requestDto = BoardRegisterRequestDto.builder()
                    .pictures(null)
                    .title("title")
                    .categoryId(1L)
                    .method(Method.SELL)
                    .price(200000)
                    .suggest(true)
                    .description("description")
                    .place("place")
                    .tmp(false)
                    .build();

            // when
            when(boardService.register(any(), anyLong())).thenThrow(MemberNotFoundException.class);

            // then
            mvc.perform(post("/board/register")
                    .content(new ObjectMapper().writeValueAsString(requestDto))
                    .contentType(MediaType.APPLICATION_JSON))
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
            BoardRegisterRequestDto requestDto = BoardRegisterRequestDto.builder()
                    .pictures(null)
                    .title("title")
                    .categoryId(1L)
                    .method(Method.SELL)
                    .price(200000)
                    .suggest(true)
                    .description("description")
                    .place("place")
                    .tmp(false)
                    .build();

            // when
            when(boardService.register(any(), anyLong())).thenThrow(CategoryNotFoundException.class);

            // then
            mvc.perform(post("/board/register")
                    .content(new ObjectMapper().writeValueAsString(requestDto))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(CATEGORY_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }
    }
}