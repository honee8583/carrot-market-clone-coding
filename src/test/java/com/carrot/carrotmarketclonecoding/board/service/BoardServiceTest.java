package com.carrot.carrotmarketclonecoding.board.service;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.MEMBER_NOT_FOUND;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.domain.Category;
import com.carrot.carrotmarketclonecoding.board.domain.enums.Method;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.board.repository.BoardRepository;
import com.carrot.carrotmarketclonecoding.board.repository.CategoryRepository;
import com.carrot.carrotmarketclonecoding.board.service.impl.BoardServiceImpl;
import com.carrot.carrotmarketclonecoding.common.exception.CategoryNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.response.FailedMessage;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @InjectMocks
    private BoardServiceImpl boardService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Nested
    @DisplayName("게시글 작성 서비스 테스트")
    class RegisterBoard {

        @Test
        @DisplayName("성공")
        void registerBoard() {
            // given
            Long memberId = 1L;
            Long categoryId = 1L;
            Long boardId = 1L;
            BoardRegisterRequestDto boardRegisterRequestDto = createRegisterRequestDto(categoryId);

            Member mockMember = Member.builder().id(memberId).build();
            Category mockCategory = Category.builder().id(categoryId).build();
            Board mockBoard = Board.builder().id(boardId).build();

            when(memberRepository.findById(any())).thenReturn(Optional.of(mockMember));
            when(categoryRepository.findById(any())).thenReturn(Optional.of(mockCategory));
            when(boardRepository.save(any())).thenReturn(mockBoard);

            // when
            Long registeredBoardId = boardService.register(boardRegisterRequestDto, memberId);

            // then
            assertThat(registeredBoardId).isEqualTo(boardId);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 작성자")
        void registerBoardMemberNotFound() {
            // given
            Long memberId = 1L;
            Long categoryId = 1L;
            BoardRegisterRequestDto boardRegisterRequestDto = createRegisterRequestDto(categoryId);

            Member mockMember = Member.builder().id(memberId).build();

            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(mockMember));
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.register(boardRegisterRequestDto, memberId))
                    .isInstanceOf(CategoryNotFoundException.class)
                    .hasMessage(FailedMessage.CATEGORY_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 카테고리")
        void registerBoardCategoryNotFound() {
            // given
            Long memberId = 1L;
            Long categoryId = 1L;
            BoardRegisterRequestDto boardRegisterRequestDto = createRegisterRequestDto(categoryId);

            when(memberRepository.findById(any())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.register(boardRegisterRequestDto, memberId))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }

        private BoardRegisterRequestDto createRegisterRequestDto(Long categoryId) {
            return BoardRegisterRequestDto.builder()
                    .pictures(null)
                    .title("title")
                    .categoryId(categoryId)
                    .method(Method.SELL)
                    .price(200000)
                    .suggest(true)
                    .description("description")
                    .place("place")
                    .tmp(false)
                    .build();
        }
    }
}