package com.carrot.carrotmarketclonecoding.keyword.repository;

import com.carrot.carrotmarketclonecoding.keyword.domain.Keyword;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    int countByMember(Member member);
}
