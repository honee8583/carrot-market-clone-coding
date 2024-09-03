package com.carrot.carrotmarketclonecoding.board.domain;

import com.carrot.carrotmarketclonecoding.board.domain.enums.Method;
import com.carrot.carrotmarketclonecoding.board.domain.enums.Status;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardUpdateRequestDto;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;

@Getter
@Builder
@ToString(exclude = "boardPictures")
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

    private Integer price;
    private Boolean suggest;
    private String description;
    private String place;

    @Builder.Default
    private int visit = 0;

    @Builder.Default
    private Boolean hide = false;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Status status = Status.SELL;

    private Boolean tmp;

    @Builder.Default
    @BatchSize(size = 1000)
    @OneToMany(mappedBy = "board")
    private List<BoardPicture> boardPictures = new ArrayList<>();

    @Builder.Default
    @BatchSize(size = 1000)
    @OneToMany(mappedBy = "board")
    private List<BoardLike> boardLikes = new ArrayList<>();

    public void increaseVisit() {
        this.visit += 1;
    }

    public static Board createBoard(BoardRegisterRequestDto registerRequestDto, Member member, Category category, boolean tmp) {
        return Board.builder()
                .title(registerRequestDto.getTitle())
                .member(member)
                .category(category)
                .method(registerRequestDto.getMethod())
                .price(registerRequestDto.getPrice())
                .suggest(registerRequestDto.getSuggest())
                .description(registerRequestDto.getDescription())
                .place(registerRequestDto.getPlace())
                .tmp(tmp)
                .build();
    }

    public void update(BoardUpdateRequestDto updateRequestDto, Category category) {
        this.title = updateRequestDto.getTitle();
        this.category = category;
        this.method = updateRequestDto.getMethod();
        this.price = updateRequestDto.getPrice();
        this.suggest = updateRequestDto.getSuggest();
        this.description = updateRequestDto.getDescription();
        this.place = updateRequestDto.getPlace();

        if (updateRequestDto.getMethod() == Method.SHARE) {
            this.price = 0;
        }

        if (this.tmp) {
            this.tmp = false;
        }
    }

    public void setPriceZeroIfMethodIsShare() {
        if (this.method == Method.SHARE) this.price = 0;
    }
}
