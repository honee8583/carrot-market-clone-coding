package com.carrot.carrotmarketclonecoding.category.repository;

import com.carrot.carrotmarketclonecoding.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
