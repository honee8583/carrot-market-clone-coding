package com.carrot.carrotmarketclonecoding.board.repository;

import com.carrot.carrotmarketclonecoding.board.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
