package com.carrot.carrotmarketclonecoding.board.service.impl;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardSearchRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardUpdateRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.MyBoardSearchRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardDetailResponseDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardSearchResponseDto;
import com.carrot.carrotmarketclonecoding.board.repository.BoardLikeRepository;
import com.carrot.carrotmarketclonecoding.board.repository.BoardPictureRepository;
import com.carrot.carrotmarketclonecoding.board.repository.BoardRepository;
import com.carrot.carrotmarketclonecoding.board.service.BoardNotificationService;
import com.carrot.carrotmarketclonecoding.board.service.BoardService;
import com.carrot.carrotmarketclonecoding.board.service.SearchKeywordRedisService;
import com.carrot.carrotmarketclonecoding.board.service.VisitRedisService;
import com.carrot.carrotmarketclonecoding.board.util.HeaderUtil;
import com.carrot.carrotmarketclonecoding.category.domain.Category;
import com.carrot.carrotmarketclonecoding.category.repository.CategoryRepository;
import com.carrot.carrotmarketclonecoding.common.exception.BoardNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.CategoryNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.TmpBoardNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.UnauthorizedAccessException;
import com.carrot.carrotmarketclonecoding.common.response.PageResponseDto;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final SearchKeywordRedisService searchKeywordRedisService;
    private final BoardPictureService boardPictureService;
    private final BoardNotificationService boardNotificationService;

    @Override
    public Long register(Long authId, BoardRegisterRequestDto registerRequestDto, MultipartFile[] pictures, boolean tmp) {
        Member member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
        Category category = findCategoryIfCategoryIdNotNull(registerRequestDto.getCategoryId());
        Board board = Board.createBoard(registerRequestDto, member, category, tmp);
        board.setPriceZeroIfMethodIsShare();
        boardRepository.save(board);
        boardPictureService.uploadPicturesIfExistAndUnderLimit(pictures, board);
        boardNotificationService.sendKeywordNotification(registerRequestDto, board);
        return board.getId();
    }

    @Override
    public BoardDetailResponseDto getBoardDetail(Long boardId, HttpServletRequest request) {
        Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);
        int like = boardLikeRepository.countByBoard(board);

        String ip = HeaderUtil.getClientIp(request);
        log.debug("IP: {}", ip);
        String userAgent = HeaderUtil.getUserAgent(request);
        log.debug("USER-AGENT: {}", userAgent);
        if (visitRedisService.increaseVisit(board.getId().toString(), ip, userAgent)) {
            board.increaseVisit();
        }

        // TODO count chats

        return BoardDetailResponseDto.createBoardDetail(board, like);
    }

    @Override
    @Transactional(readOnly = true)
    public BoardDetailResponseDto getTmpBoardDetail(Long authId) {
        Member member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
        Board tmpBoard = boardRepository.findFirstByMemberAndTmpIsTrueOrderByCreateDateDesc(member)
                .orElseThrow(TmpBoardNotFoundException::new);
        return BoardDetailResponseDto.createBoardDetail(tmpBoard, 0);
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
    public void update(Long boardId, Long authId, BoardUpdateRequestDto updateRequestDto, MultipartFile[] newPictures) {
        Member member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
        Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);
        isWriterOfBoard(board, member);
        if (board.getTmp()) {
            boardRepository.deleteAllByMemberAndTmpIsTrueAndIdIsNot(member, boardId);
        }

        Category category = categoryRepository.findById(updateRequestDto.getCategoryId()).orElseThrow(CategoryNotFoundException::new);
        board.update(updateRequestDto, category);

        boardPictureService.deletePicturesIfExist(updateRequestDto.getRemovePictures());
        boardPictureService.uploadPicturesIfExistAndUnderLimit(newPictures, board);
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
}
