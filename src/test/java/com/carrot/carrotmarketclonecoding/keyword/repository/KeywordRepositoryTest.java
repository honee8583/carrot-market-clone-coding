package com.carrot.carrotmarketclonecoding.keyword.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.carrot.carrotmarketclonecoding.keyword.domain.Keyword;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class KeywordRepositoryTest {

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("쿼리메서드 countByMember() 테스트")
    void countByMember() {
        // given

        Member mockMember = Member.builder().authId(1111L).build();
        memberRepository.save(mockMember);

        keywordRepository.saveAll(Arrays.asList(
                Keyword.builder().name("keyword1").member(mockMember).build(),
                Keyword.builder().name("keyword2").member(mockMember).build(),
                Keyword.builder().name("keyword3").member(mockMember).build()
        ));

        // when
        int count = keywordRepository.countByMember(mockMember);

        // then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("쿼리메서드 findAlByMember() 테스트")
    void findAllByMember() {
        // given
        // when
        // then
    }
}