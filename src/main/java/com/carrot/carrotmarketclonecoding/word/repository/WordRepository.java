package com.carrot.carrotmarketclonecoding.word.repository;

import com.carrot.carrotmarketclonecoding.word.domain.Word;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRepository extends JpaRepository<Word, Long> {

}
