package com.carrot.carrotmarketclonecoding.board.service;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.domain.BoardLike;
import com.carrot.carrotmarketclonecoding.board.repository.BoardLikeRepository;
import com.carrot.carrotmarketclonecoding.board.repository.BoardRepository;
import com.carrot.carrotmarketclonecoding.board.service.impl.BoardLikeServiceImpl;
import com.carrot.carrotmarketclonecoding.common.exception.BoardNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BoardLikeServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private BoardLikeRepository boardLikeRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private BoardLikeServiceImpl boardLikeService;

    @Nested
    @DisplayName("관심 게시글 등록 서비스 테스트")
    class AddBoardLike {

        @Test
        @DisplayName("성공")
        void addBoardLikeSuccess() {
            // given
            Long boardId = 1L;
            Long memberId = 1L;
            Board mockBoard = Board.builder().id(boardId).build();
            Member mockMember = Member.builder().id(memberId).build();

            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(mockMember));

            // when
            boardLikeService.add(boardId, memberId);

            // then
            ArgumentCaptor<BoardLike> argumentCaptor = ArgumentCaptor.forClass(BoardLike.class);
            verify(boardLikeRepository).save(argumentCaptor.capture());
            BoardLike boardLike = argumentCaptor.getValue();
            assertThat(boardLike.getBoard()).isEqualTo(mockBoard);
            assertThat(boardLike.getMember()).isEqualTo(mockMember);
        }

        @Test
        @DisplayName("실패 - 게시글 존재하지 않음")
        void addBoardLikeFailBoardNotFound() {
            // given
            Long boardId = 1L;
            Long memberId = 1L;
            when(boardRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardLikeService.add(boardId, memberId))
                    .isInstanceOf(BoardNotFoundException.class)
                    .hasMessage(BOARD_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("실패 - 사용자 존재하지 않음")
        void addBoardLikeFailMemberNotFound() {
            // given
            Long boardId = 1L;
            Long memberId = 1L;
            Board mockBoard = Board.builder().id(boardId).build();

            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));
            when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardLikeService.add(boardId, memberId))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }
    }
}