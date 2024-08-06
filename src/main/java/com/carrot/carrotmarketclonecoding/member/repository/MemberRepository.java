package com.carrot.carrotmarketclonecoding.member.repository;

import com.carrot.carrotmarketclonecoding.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
