package com.carrot.carrotmarketclonecoding.board.domain;

import com.carrot.carrotmarketclonecoding.board.domain.enums.Method;
import com.carrot.carrotmarketclonecoding.board.domain.enums.Status;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.common.BaseEntity;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "boards")
public class Board extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @Enumerated(EnumType.STRING)
    private Method method;

    private int price;
    private Boolean suggest;
    private String description;
    private String place;

    @Builder.Default
    private int visit = 0;

    @Builder.Default
    private Boolean hide = false;   // 수정만 가능

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Status status = Status.SELL;  // 수정만 가능

    private Boolean tmp;

    public void increaseVisit() {
        this.visit += 1;
    }

    public static Board createBoard(BoardRegisterRequestDto boardInputRequestDto, Member member, Category category) {
        return Board.builder()
                .title(boardInputRequestDto.getTitle())
                .member(member)
                .category(category)
                .method(boardInputRequestDto.getMethod())
                .price(boardInputRequestDto.getPrice())
                .suggest(boardInputRequestDto.getSuggest())
                .description(boardInputRequestDto.getDescription())
                .place(boardInputRequestDto.getPlace())
                .tmp(boardInputRequestDto.getTmp())
                .build();
    }
}
