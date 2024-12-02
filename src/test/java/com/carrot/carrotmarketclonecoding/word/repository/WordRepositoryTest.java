package com.carrot.carrotmarketclonecoding.word.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.carrot.carrotmarketclonecoding.RepositoryTest;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.word.domain.Word;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WordRepositoryTest extends RepositoryTest {

    @Test
    @DisplayName("쿼리메서드 findAllByMember() 테스트")
    void findAllByMember() {
        // given
        Member member = new Member();
        memberRepository.save(member);

        Word word1 = Word.builder().member(member).build();
        Word word2 = Word.builder().member(member).build();
        Word word3 = Word.builder().member(member).build();
        wordRepository.save(word1);
        wordRepository.save(word2);
        wordRepository.save(word3);

        // when
        List<Word> words = wordRepository.findAllByMember(member);

        // then
        assertThat(words.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("쿼리메서드 countByMember() 테스트")
    void countByMember() {
        // given
        Member member = new Member();
        memberRepository.save(member);

        Word word1 = Word.builder().member(member).build();
        Word word2 = Word.builder().member(member).build();
        Word word3 = Word.builder().member(member).build();
        wordRepository.save(word1);
        wordRepository.save(word2);
        wordRepository.save(word3);

        // when
        int total = wordRepository.countByMember(member);

        // then
        assertThat(total).isEqualTo(3);
    }
}