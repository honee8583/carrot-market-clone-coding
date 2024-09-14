package com.carrot.carrotmarketclonecoding.board.repository;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {
    void deleteAllByMemberAndTmpIsTrueAndIdIsNot(Member member, Long id);
    Optional<Board> findFirstByMemberAndTmpIsTrueOrderByCreateDateDesc(Member member);

    @Modifying
    @Query("DELETE FROM Board b WHERE b.member.id = :memberId")
    void deleteAllByMemberId(Long memberId);
}
