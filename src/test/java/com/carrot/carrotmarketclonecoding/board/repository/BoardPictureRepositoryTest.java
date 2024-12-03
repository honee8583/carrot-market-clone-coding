package com.carrot.carrotmarketclonecoding.board.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.carrot.carrotmarketclonecoding.RepositoryTest;
import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.domain.BoardPicture;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BoardPictureRepositoryTest extends RepositoryTest {

    @Test
    @DisplayName("쿼리메소드 findByBoard() 테스트")
    void findByBoard() {
        // given
        Board board = boardRepository.save(Board.builder().build());
        boardPictureRepository.save(BoardPicture.builder().board(board).build());
        boardPictureRepository.save(BoardPicture.builder().board(board).build());

        // when
        List<BoardPicture> boardPictures = boardPictureRepository.findByBoard(board);

        // then
        assertThat(boardPictures.size()).isEqualTo(2);
    }
}