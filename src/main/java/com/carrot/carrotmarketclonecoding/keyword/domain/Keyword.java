package com.carrot.carrotmarketclonecoding.keyword.domain;

import com.carrot.carrotmarketclonecoding.category.domain.Category;
import com.carrot.carrotmarketclonecoding.common.BaseEntity;
import com.carrot.carrotmarketclonecoding.keyword.dto.KeywordRequestDto.KeywordCreateRequestDto;
import com.carrot.carrotmarketclonecoding.keyword.dto.KeywordRequestDto.KeywordEditRequestDto;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "keywords")
public class Keyword extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    private String name;

    private Integer minPrice;

    private Integer maxPrice;

    public static Keyword createKeyword(KeywordCreateRequestDto createRequestDto, Member member) {
        return Keyword.builder()
                .member(member)
                .name(createRequestDto.getName())
                .build();
    }

    public void modify(Category category, KeywordEditRequestDto editRequestDto) {
        this.category = category;
        this.name = editRequestDto.getName();
        this.minPrice = editRequestDto.getMinPrice();
        this.maxPrice = editRequestDto.getMaxPrice();
    }

    public Long getCategoryId() {
        if (this.category != null) {
            return this.category.getId();
        }
        return null;
    }
}
