package com.carrot.carrotmarketclonecoding.word.repository;

import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.word.domain.Word;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRepository extends JpaRepository<Word, Long> {
    List<Word> findAllByMember(Member member);
    Integer countByMember(Member member);
}
