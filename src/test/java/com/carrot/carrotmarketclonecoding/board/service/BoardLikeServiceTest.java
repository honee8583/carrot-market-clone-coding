package com.carrot.carrotmarketclonecoding.board.service;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.domain.BoardLike;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardSearchResponseDto;
import com.carrot.carrotmarketclonecoding.board.repository.BoardLikeRepository;
import com.carrot.carrotmarketclonecoding.board.repository.BoardRepository;
import com.carrot.carrotmarketclonecoding.board.service.impl.BoardLikeServiceImpl;
import com.carrot.carrotmarketclonecoding.common.exception.BoardNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberAlreadyLikedBoardException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.response.PageResponseDto;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import com.carrot.carrotmarketclonecoding.notification.domain.enums.NotificationType;
import com.carrot.carrotmarketclonecoding.notification.service.impl.NotificationServiceImpl;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BoardLikeServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private BoardLikeRepository boardLikeRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private NotificationServiceImpl notificationService;

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
            Long targetId = 2L;
            Board mockBoard = Board.builder()
                    .id(1L)
                    .member(Member.builder()
                            .id(2L)
                            .authId(targetId)
                            .nickname("target")
                            .build())
                    .build();
            Member mockMember = Member.builder()
                    .id(1L)
                    .authId(memberId)
                    .nickname("member")
                    .build();
            String content = String.format("%s님이 %s님의 게시글을 좋아하였습니다!", mockMember.getNickname(), mockBoard.getMember().getNickname());

            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));

            // when
            boardLikeService.add(boardId, memberId);

            // then
            ArgumentCaptor<BoardLike> argumentCaptor = ArgumentCaptor.forClass(BoardLike.class);
            verify(boardLikeRepository).save(argumentCaptor.capture());
            BoardLike boardLike = argumentCaptor.getValue();
            assertThat(boardLike.getBoard().getId()).isEqualTo(1L);
            assertThat(boardLike.getMember().getId()).isEqualTo(1L);
            verify(notificationService, times(1)).add(targetId, NotificationType.LIKE, content);
        }

        @Test
        @DisplayName("실패 - 게시글 존재하지 않음")
        void addBoardLikeFailBoardNotFound() {
            // given
            when(boardRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardLikeService.add(1L, 1L))
                    .isInstanceOf(BoardNotFoundException.class)
                    .hasMessage(BOARD_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("실패 - 사용자 존재하지 않음")
        void addBoardLikeFailMemberNotFound() {
            // given
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mock(Board.class)));
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardLikeService.add(1L, 1L))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("실패 - 사용자가 이미 관심게시글로 등록한 게시글임")
        void addBoardLikeFailMemberAlreadyLikedBoard() {
            // given
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mock(Board.class)));
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mock(Member.class)));
            when(boardLikeRepository.findByBoardAndMember(any(), any())).thenReturn(Optional.of(mock(BoardLike.class)));

            // when
            // then
            assertThatThrownBy(() -> boardLikeService.add(1L, 1L))
                    .isInstanceOf(MemberAlreadyLikedBoardException.class)
                    .hasMessage(MEMBER_ALREADY_LIKED_BOARD.getMessage());
        }
    }

    @Nested
    @DisplayName("사용자가 등록한 관심게시글 목록 조회")
    class MemberLikedBoards {

        @Test
        @DisplayName("성공")
        void memberLikedBoardsSuccess() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            List<BoardSearchResponseDto> boardSearchResponse = Arrays.asList(
                    new BoardSearchResponseDto(),
                    new BoardSearchResponseDto()
            );

            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mock(Member.class)));
            when(boardRepository.searchMemberLikedBoards(any(), any())).thenReturn(new PageImpl<>(boardSearchResponse, pageable, boardSearchResponse.size()));

            // when
            PageResponseDto<BoardSearchResponseDto> result = boardLikeService.getMemberLikedBoards(1L, pageable);

            // then
            assertThat(result.getContents().size()).isEqualTo(2);
        }

        @Test
        @DisplayName("실패 - 사용자가 존재하지 않음")
        void memberLikedBoardsFailMemberNotFound() {
            // given
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardLikeService.getMemberLikedBoards(1L, PageRequest.of(0, 10)))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }
    }
}