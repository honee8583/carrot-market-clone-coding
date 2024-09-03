package com.carrot.carrotmarketclonecoding.board.repository;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.domain.BoardPicture;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BoardPictureRepository extends JpaRepository<BoardPicture, Long> {
    List<BoardPicture> findByBoard(Board board);

    @Modifying
    @Query("DELETE FROM BoardPicture bp WHERE bp.board.id = :boardId")
    void deleteAllByBoardId(Long boardId);
}
