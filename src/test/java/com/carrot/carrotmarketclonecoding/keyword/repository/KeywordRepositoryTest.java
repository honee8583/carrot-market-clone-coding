package com.carrot.carrotmarketclonecoding.keyword.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.carrot.carrotmarketclonecoding.RepositoryTest;
import com.carrot.carrotmarketclonecoding.keyword.domain.Keyword;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class KeywordRepositoryTest extends RepositoryTest {

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
    @DisplayName("쿼리메서드 findAllByMember() 테스트")
    void findAllByMember() {
        // given
        Member mockMember = Member.builder()
                .authId(1111L)
                .build();
        memberRepository.save(mockMember);

        keywordRepository.saveAll(Arrays.asList(
                Keyword.builder().member(mockMember).build(),
                Keyword.builder().member(mockMember).build()
        ));

        // when
        List<Keyword> keywords = keywordRepository.findAllByMember(mockMember);

        // then
        assertThat(keywords.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("쿼리메서드 findByNameIn() 테스트")
    void findByNameIn() {
        // given
        Set<String> words = new HashSet<>(Arrays.asList(
             "keyword1", "keyword2", "keyword3"
        ));

        keywordRepository.saveAll(Arrays.asList(
                Keyword.builder().name("keyword1").build(),
                Keyword.builder().name("keyword2").build(),
                Keyword.builder().name("keyword3").build(),
                Keyword.builder().name("keyword4").build(),
                Keyword.builder().name("keyword5").build()
        ));

        // when
        Set<Keyword> keywords = keywordRepository.findByNameIn(words);

        // then
        assertThat(keywords.size()).isEqualTo(3);
    }
}