package com.carrot.carrotmarketclonecoding.board.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.carrot.carrotmarketclonecoding.RepositoryTest;
import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.domain.BoardLike;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BoardLikeRepositoryTest extends RepositoryTest {

    @Test
    @DisplayName("쿼리메소드 countByBoard() 테스트")
    void countByBoard() {
        // given
        Board board = boardRepository.save(Board.builder().build());
        boardLikeRepository.save(BoardLike.builder().board(board).build());
        boardLikeRepository.save(BoardLike.builder().board(board).build());

        // when
        int result = boardLikeRepository.countByBoard(board);

        // then
        assertThat(result).isEqualTo(2);
    }

    @Test
    @DisplayName("쿼리메소드 findByBoardAndMember() 테스트")
    void findByBoardAndMember() {
        // given
        Board board = boardRepository.save(new Board());
        Member member = memberRepository.save(new Member());
        BoardLike boardLike = BoardLike.builder()
                .board(board)
                .member(member)
                .build();

        boardLikeRepository.save(boardLike);

        // when
        Optional<BoardLike> savedBoardLike = boardLikeRepository.findByBoardAndMember(board, member);

        // then
        assertThat(savedBoardLike.isPresent()).isTrue();
        assertThat(savedBoardLike.get().getBoard()).isEqualTo(board);
        assertThat(savedBoardLike.get().getMember()).isEqualTo(member);
    }
}