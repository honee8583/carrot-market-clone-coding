package com.carrot.carrotmarketclonecoding.board.repository;

import static org.assertj.core.api.Assertions.*;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.config.JpaAuditingConfig;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@Import(JpaAuditingConfig.class)
@DataJpaTest
@Transactional
class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("쿼리메소드 deleteAllByMemberAndTmpIsTrueAndIdIsNot() 테스트")
    void deleteAllByMemberAndTmpIsTrueAndIdIsNot() {
        // given
        Member member = Member.builder().build();
        memberRepository.save(member);

        Board board1 = Board.builder().member(member).tmp(true).build();
        Board board2 = Board.builder().member(member).tmp(true).build();
        Board board3 = Board.builder().member(member).tmp(true).build();
        Board board4 = Board.builder().member(member).tmp(true).build();
        boardRepository.save(board1);
        boardRepository.save(board2);
        boardRepository.save(board3);
        boardRepository.save(board4);

        // when
        boardRepository.deleteAllByMemberAndTmpIsTrueAndIdIsNot(member, board4.getId());

        // then
        assertThat(boardRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("쿼리메소드 findFirstByMemberAndTmpIsTrueOrderByCreateDateDesc() 테스트")
    void findFirstByMemberAndTmpIsTrueOrderByCreateDateDesc() throws Exception {
        // given
        Member member = Member.builder().build();
        memberRepository.save(member);

        Board board1 = Board.builder().member(member).tmp(false).build();
        Board board2 = Board.builder().member(member).tmp(true).build();
        Board board3 = Board.builder().member(member).tmp(true).build();
        boardRepository.save(board1);
        boardRepository.save(board2);
        boardRepository.save(board3);

        // when
        Optional<Board> board = boardRepository.findFirstByMemberAndTmpIsTrueOrderByCreateDateDesc(member);

        // then
        assertThat(board.isPresent()).isTrue();
        assertThat(board.get().getId()).isEqualTo(3L);
    }
}