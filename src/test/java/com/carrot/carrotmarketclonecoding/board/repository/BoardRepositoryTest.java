package com.carrot.carrotmarketclonecoding.board.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.carrot.carrotmarketclonecoding.RepositoryTest;
import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BoardRepositoryTest extends RepositoryTest {

    @PersistenceContext
    EntityManager em;

    @BeforeEach
    public void setUp() {
        tearDown();
    }

    private void tearDown() {
        em.createNativeQuery("ALTER TABLE MEMBERS ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE BOARDS ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }

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
    void findFirstByMemberAndTmpIsTrueOrderByCreateDateDesc() {
        // given
        Member member = Member.builder().build();
        memberRepository.save(member);

        Board board1 = Board.builder().id(1L).member(member).tmp(false).build();
        Board board2 = Board.builder().id(2L).member(member).tmp(true).build();
        Board board3 = Board.builder().id(3L).member(member).tmp(true).build();
        boardRepository.save(board1);
        boardRepository.save(board2);
        boardRepository.save(board3);

        // when
        Optional<Board> board = boardRepository.findFirstByMemberAndTmpIsTrueOrderByCreateDateDesc(member);

        // then
        assertThat(board.isPresent()).isTrue();
        assertThat(board.get().getId()).isEqualTo(board3.getId());
    }
}