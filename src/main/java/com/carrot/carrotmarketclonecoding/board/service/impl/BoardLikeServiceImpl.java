package com.carrot.carrotmarketclonecoding.board.service.impl;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.domain.BoardLike;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardSearchResponseDto;
import com.carrot.carrotmarketclonecoding.board.repository.BoardLikeRepository;
import com.carrot.carrotmarketclonecoding.board.repository.BoardRepository;
import com.carrot.carrotmarketclonecoding.board.service.BoardLikeService;
import com.carrot.carrotmarketclonecoding.common.exception.BoardNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberAlreadyLikedBoardException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.response.PageResponseDto;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardLikeServiceImpl implements BoardLikeService {
    private final BoardLikeRepository boardLikeRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Override
    public void add(Long boardId, Long authId) {
        Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);
        Member member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);

        Optional<BoardLike> boardLike = boardLikeRepository.findByBoardAndMember(board, member);
        if (boardLike.isPresent()) {
            throw new MemberAlreadyLikedBoardException();
        }

        boardLikeRepository.save(BoardLike.builder()
                .board(board)
                .member(member)
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<BoardSearchResponseDto> getMemberLikedBoards(Long authId, Pageable pageable) {
        Member member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
        return new PageResponseDto<>(boardRepository.searchMemberLikedBoards(member, pageable));
    }
}
