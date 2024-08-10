package com.carrot.carrotmarketclonecoding.board.repository;

import static org.assertj.core.api.Assertions.*;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.domain.BoardLike;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
class BoardLikeRepositoryTest {

    @Autowired
    private BoardLikeRepository boardLikeRepository;

    @Autowired
    private BoardRepository boardRepository;

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
}