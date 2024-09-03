package com.carrot.carrotmarketclonecoding.board.repository.impl;

import static com.carrot.carrotmarketclonecoding.board.domain.QBoard.board;
import static com.carrot.carrotmarketclonecoding.board.domain.QBoardLike.boardLike;
import static com.carrot.carrotmarketclonecoding.board.domain.enums.SearchOrder.*;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.domain.enums.SearchOrder;
import com.carrot.carrotmarketclonecoding.board.domain.enums.Status;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardSearchRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.MyBoardSearchRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardSearchResponseDto;
import com.carrot.carrotmarketclonecoding.board.repository.BoardRepositoryCustom;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@Slf4j
public class BoardRepositoryCustomImpl implements BoardRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public BoardRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, em);
    }

    @Override
    public Page<BoardSearchResponseDto> findAllByMemberAndSearchRequestDto(Member member, BoardSearchRequestDto searchRequestDto, Pageable pageable) {
        List<Board> boards = queryFactory
                .selectFrom(board)
                .where(
                        memberEq(member),
                        titleContains(searchRequestDto.getKeyword()),
                        priceBetween(searchRequestDto.getMinPrice(), searchRequestDto.getMaxPrice()),
                        categoryEq(searchRequestDto.getCategoryId()),
                        board.tmp.eq(false)
                )
                .orderBy(getOrder(searchRequestDto.getOrder()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .groupBy(board.id)
                .fetch();

        JPAQuery<Long> total = queryFactory
                .select(board.count())
                .from(board)
                .where(
                        memberEq(member),
                        titleContains(searchRequestDto.getKeyword()),
                        priceBetween(searchRequestDto.getMinPrice(), searchRequestDto.getMaxPrice()),
                        categoryEq(searchRequestDto.getCategoryId()),
                        board.tmp.eq(false));

        return PageableExecutionUtils.getPage(boards.stream()
                .map(BoardSearchResponseDto::getSearchResult)
                .collect(Collectors.toList()), pageable, total::fetchOne);
    }

    @Override
    public Page<BoardSearchResponseDto> searchMemberLikedBoards(Member member, Pageable pageable) {
        List<Board> boards = queryFactory
                .selectFrom(board)
                .leftJoin(boardLike).on(board.id.eq(boardLike.board.id))
                .where(boardLike.member.eq(member))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .groupBy(board.id)
                .fetch();

        JPAQuery<Long> total = queryFactory
                .select(board.count())
                .from(board)
                .leftJoin(boardLike).on(board.id.eq(boardLike.board.id))
                .where(boardLike.member.eq(member))
                .groupBy(board.id);

        return PageableExecutionUtils.getPage(boards.stream()
                .map(BoardSearchResponseDto::getSearchResult)
                .collect(Collectors.toList()), pageable, total::fetchOne);
    }

    @Override
    public Page<BoardSearchResponseDto> findAllByStatusOrHide(Member member, MyBoardSearchRequestDto searchRequestDto, Pageable pageable) {
        List<Board> boards = queryFactory
                .selectFrom(board)
                .where(
                        memberEq(member),
                        statusEq(searchRequestDto.getStatus()),
                        hideEq(searchRequestDto.getHide()),
                        board.tmp.eq(false)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .groupBy(board.id)
                .fetch();

        JPAQuery<Long> total = queryFactory
                .select(board.count())
                .from(board)
                .where(
                        memberEq(member),
                        statusEq(searchRequestDto.getStatus()),
                        hideEq(searchRequestDto.getHide()),
                        board.tmp.eq(false));

        return PageableExecutionUtils.getPage(boards.stream()
                .map(BoardSearchResponseDto::getSearchResult)
                .collect(Collectors.toList()), pageable, total::fetchOne);
    }

    private BooleanExpression hideEq(Boolean hide) {
        return hide != null ? board.hide.eq(hide) : null;
    }

    private BooleanExpression memberEq(Member member) {
        return member != null ? board.member.eq(member) : null;
    }

    private BooleanExpression statusEq(Status status) {
        return status != null ? board.status.eq(status) : null;
    }

    private BooleanExpression titleContains(String keyword) {
        return StringUtils.isEmpty(keyword) ? null : board.title.contains(keyword);
    }

    private BooleanExpression priceBetween(Integer minPrice, Integer maxPrice) {
        return minPrice == null && maxPrice == null ? null : board.price.between(minPrice, maxPrice);
    }

    private BooleanExpression categoryEq(Long categoryId) {
        return categoryId != null ? board.category.id.eq(categoryId) : null;
    }

    private OrderSpecifier<?> getOrder(SearchOrder order) {
        if (order == null) {
            return board.id.desc();
        } else if (order.equals(NEWEST)) {
            return board.createDate.desc();
        } else {
            return board.createDate.asc();
        }
    }
}
