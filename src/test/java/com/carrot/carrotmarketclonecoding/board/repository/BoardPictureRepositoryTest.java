package com.carrot.carrotmarketclonecoding.board.repository;

import static org.assertj.core.api.Assertions.*;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.domain.BoardPicture;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
class BoardPictureRepositoryTest {

    @Autowired
    private BoardPictureRepository boardPictureRepository;

    @Autowired
    private BoardRepository boardRepository;

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