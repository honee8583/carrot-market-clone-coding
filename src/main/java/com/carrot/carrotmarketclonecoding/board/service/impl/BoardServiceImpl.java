package com.carrot.carrotmarketclonecoding.board.service.impl;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.category.domain.Category;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardSearchRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardUpdateRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.MyBoardSearchRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardDetailResponseDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardSearchResponseDto;
import com.carrot.carrotmarketclonecoding.board.repository.BoardLikeRepository;
import com.carrot.carrotmarketclonecoding.board.repository.BoardPictureRepository;
import com.carrot.carrotmarketclonecoding.board.repository.BoardRepository;
import com.carrot.carrotmarketclonecoding.board.repository.CategoryRepository;
import com.carrot.carrotmarketclonecoding.board.service.BoardService;
import com.carrot.carrotmarketclonecoding.board.service.SearchKeywordRedisService;
import com.carrot.carrotmarketclonecoding.board.service.VisitRedisService;
import com.carrot.carrotmarketclonecoding.common.exception.BoardNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.CategoryNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.UnauthorizedAccessException;
import com.carrot.carrotmarketclonecoding.common.response.PageResponseDto;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final BoardPictureRepository boardPictureRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final VisitRedisService visitRedisService;
    private final BoardPictureService boardPictureService;
    private final SearchKeywordRedisService searchKeywordRedisService;

    @Override
    public Long register(BoardRegisterRequestDto registerRequestDto, Long authId, boolean tmp) {
        Member member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
        Category category = findCategoryIfCategoryIdNotNull(registerRequestDto.getCategoryId());
        Board board = Board.createBoard(registerRequestDto, member, category, tmp);
        board.setPriceZeroIfMethodIsShare();
        boardRepository.save(board);
        boardPictureService.uploadPicturesIfExistAndUnderLimit(registerRequestDto.getPictures(), board);

        return board.getId();
    }

    @Override
    public BoardDetailResponseDto detail(Long boardId, HttpServletRequest request) {
        Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);
        int like = boardLikeRepository.countByBoard(board);

        String ip = getClientIp(request);
        String userAgent = getUserAgent(request);
        log.debug("IP: {}", ip);
        log.debug("USER-AGENT: {}", userAgent);

        if (visitRedisService.increaseVisit(board.getId().toString(), ip, userAgent)) {
            board.increaseVisit();
        }

        // TODO count chats

        return BoardDetailResponseDto.createBoardDetail(board, like);
    }

    @Override
    @Transactional(readOnly = true)
    public BoardDetailResponseDto tmpBoardDetail(Long authId) {
        Member member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
        Optional<Board> board = boardRepository.findFirstByMemberAndTmpIsTrueOrderByCreateDateDesc(member);

        if (board.isPresent()) {
            return BoardDetailResponseDto.createBoardDetail(board.get(), 0);
        }

        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<BoardSearchResponseDto> search(Long authId, BoardSearchRequestDto searchRequestDto, Pageable pageable) {
        Member member = null;
        if (isLogin(authId)) {
            member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
            searchKeywordRedisService.addRecentSearchKeywords(member.getId(), searchRequestDto.getKeyword());
            searchKeywordRedisService.addSearchKeywordRank(searchRequestDto.getKeyword());
        }
        return new PageResponseDto<>(boardRepository.findAllBySearchRequestDto(searchRequestDto, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<BoardSearchResponseDto> searchMyBoards(Long authId, MyBoardSearchRequestDto searchRequestDto, Pageable pageable) {
        Member member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
        return new PageResponseDto<>(boardRepository.findAllByStatusOrHide(member, searchRequestDto, pageable));
    }

    @Override
    public void update(BoardUpdateRequestDto updateRequestDto, Long boardId, Long authId) {
        Member member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
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
    public void delete(Long boardId, Long authId) {
        Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);
        Member member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
        isWriterOfBoard(board, member);
        boardLikeRepository.deleteAllByBoardId(boardId);
        boardPictureRepository.deleteAllByBoardId(boardId);
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

    private boolean isLogin(Long authId) {
        return authId != null && authId > 0L;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}
