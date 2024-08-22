package com.carrot.carrotmarketclonecoding.board.service.impl;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.domain.BoardPicture;
import com.carrot.carrotmarketclonecoding.board.domain.Category;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardSearchRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardUpdateRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardDetailResponseDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardSearchResponseDto;
import com.carrot.carrotmarketclonecoding.board.repository.BoardLikeRepository;
import com.carrot.carrotmarketclonecoding.board.repository.BoardPictureRepository;
import com.carrot.carrotmarketclonecoding.board.repository.BoardRepository;
import com.carrot.carrotmarketclonecoding.board.repository.CategoryRepository;
import com.carrot.carrotmarketclonecoding.board.service.BoardService;
import com.carrot.carrotmarketclonecoding.board.service.VisitService;
import com.carrot.carrotmarketclonecoding.common.exception.BoardNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.CategoryNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.UnauthorizedAccessException;
import com.carrot.carrotmarketclonecoding.common.response.PageResponseDto;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final BoardPictureRepository boardPictureRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final VisitService visitService;
    private final BoardPictureService boardPictureService;

    @Override
    public Long register(BoardRegisterRequestDto registerRequestDto, Long memberId, boolean tmp) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        Category category = findCategoryIfCategoryIdNotNull(registerRequestDto.getCategoryId());
        Board board = Board.createBoard(registerRequestDto, member, category, tmp);
        board.setPriceZeroIfMethodIsShare();
        boardRepository.save(board);
        boardPictureService.uploadPicturesIfExistAndUnderLimit(registerRequestDto.getPictures(), board);

        return board.getId();
    }

    @Override
    public BoardDetailResponseDto detail(Long boardId, String sessionId) {
        Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);
        List<BoardPicture> pictures = boardPictureRepository.findByBoard(board);
        int like = boardLikeRepository.countByBoard(board);

        if (visitService.increaseVisit(board.getId().toString(), sessionId)) {
            board.increaseVisit();
        }

        // TODO count chats

        return BoardDetailResponseDto.createBoardDetail(board, like);
    }

    @Override
    @Transactional(readOnly = true)
    public BoardDetailResponseDto tmpBoardDetail(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        Optional<Board> board = boardRepository.findFirstByMemberAndTmpIsTrueOrderByCreateDateDesc(member);

        if (board.isPresent()) {
            return BoardDetailResponseDto.createBoardDetail(board.get(), 0);
        }

        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<BoardSearchResponseDto> search(Long memberId, BoardSearchRequestDto searchRequestDto, Pageable pageable) {
        Member member = null;
        if (memberId != null) {
            member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        }
        return new PageResponseDto<>(boardRepository.findAllByMemberAndSearchRequestDto(member, searchRequestDto, pageable));
    }

    @Override
    public void update(BoardUpdateRequestDto updateRequestDto, Long boardId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);
        isWriterOfBoard(board, member);
        if (board.getTmp()) {
            boardRepository.deleteAllByMemberAndTmpIsTrueAndIdIsNot(member, boardId);
        }

        Category category = categoryRepository.findById(updateRequestDto.getCategoryId()).orElseThrow(CategoryNotFoundException::new);
        board.update(updateRequestDto, category);

        boardPictureService.deletePicturesIfExist(updateRequestDto.getRemovePictures());
        boardPictureService.uploadPicturesIfExistAndUnderLimit(updateRequestDto.getNewPictures(), board);
    }

    @Override
    public void delete(Long boardId, Long memberId) {
        Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        isWriterOfBoard(board, member);
        boardRepository.delete(board);
    }

    private Category findCategoryIfCategoryIdNotNull(Long categoryId) {
        if (categoryId != null && categoryId > 0) {
            return categoryRepository.findById(categoryId).orElseThrow(CategoryNotFoundException::new);
        }
        return null;

    }

    private void isWriterOfBoard(Board board, Member member) {
        if (board.getMember() != member) {
            throw new UnauthorizedAccessException();
        }
    }
}
