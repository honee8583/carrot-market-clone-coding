package com.carrot.carrotmarketclonecoding.board.repository;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.domain.BoardPicture;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardPictureRepository extends JpaRepository<BoardPicture, Long> {
    List<BoardPicture> findByBoard(Board board);
}
