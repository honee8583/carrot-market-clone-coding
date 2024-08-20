package com.carrot.carrotmarketclonecoding.board.repository;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {
    void deleteAllByMemberAndTmpIsTrueAndIdIsNot(Member member, Long id);
    Optional<Board> findFirstByMemberAndTmpIsTrueOrderByCreateDateDesc(Member member);
}
