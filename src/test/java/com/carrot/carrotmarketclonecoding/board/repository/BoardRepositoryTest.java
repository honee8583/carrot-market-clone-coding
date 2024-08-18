package com.carrot.carrotmarketclonecoding.board.repository;

import static org.assertj.core.api.Assertions.*;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("쿼리메소드 deleteAllByMemberAndTmpIsTrueAndIdIsNot() 테스트")
    void testDeleteAllByMemberAndTmpIsTrueAndIdIsNot() {
        // given
        Member member = Member.builder().id(1L).build();
        memberRepository.save(member);

        Board board1 = Board.builder()
                .member(member)
                .tmp(true)
                .build();
        Board board2 = Board.builder()
                .member(member)
                .tmp(true)
                .build();
        Board board3 = Board.builder()
                .member(member)
                .tmp(true)
                .build();
        Board board4 = Board.builder()
                .member(member)
                .tmp(true)
                .build();
        boardRepository.save(board1);
        boardRepository.save(board2);
        boardRepository.save(board3);
        boardRepository.save(board4);

        // when
        boardRepository.deleteAllByMemberAndTmpIsTrueAndIdIsNot(member, 3L);

        // then
        assertThat(boardRepository.count()).isEqualTo(1);
    }
}