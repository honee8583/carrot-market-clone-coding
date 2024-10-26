package com.carrot.carrotmarketclonecoding.board.service.impl;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardSearchRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardUpdateRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.MyBoardSearchRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardDetailResponseDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardNotificationResponseDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardSearchResponseDto;
import com.carrot.carrotmarketclonecoding.board.repository.BoardLikeRepository;
import com.carrot.carrotmarketclonecoding.board.repository.BoardPictureRepository;
import com.carrot.carrotmarketclonecoding.board.repository.BoardRepository;
import com.carrot.carrotmarketclonecoding.board.service.BoardService;
import com.carrot.carrotmarketclonecoding.board.service.SearchKeywordRedisService;
import com.carrot.carrotmarketclonecoding.board.service.VisitRedisService;
import com.carrot.carrotmarketclonecoding.category.domain.Category;
import com.carrot.carrotmarketclonecoding.category.repository.CategoryRepository;
import com.carrot.carrotmarketclonecoding.common.exception.BoardNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.CategoryNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.UnauthorizedAccessException;
import com.carrot.carrotmarketclonecoding.common.response.PageResponseDto;
import com.carrot.carrotmarketclonecoding.keyword.domain.Keyword;
import com.carrot.carrotmarketclonecoding.keyword.repository.KeywordRepository;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import com.carrot.carrotmarketclonecoding.notification.domain.enums.NotificationType;
import com.carrot.carrotmarketclonecoding.notification.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    private final KeywordRepository keywordRepository;
    private final VisitRedisService visitRedisService;
    private final SearchKeywordRedisService searchKeywordRedisService;
    private final BoardPictureService boardPictureService;
    private final NotificationService notificationService;

    private static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String HEADER_USER_AGENT = "User-Agent";

    @Override
    public Long register(Long authId, BoardRegisterRequestDto registerRequestDto, MultipartFile[] pictures, boolean tmp) {
        Member member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
        Category category = findCategoryIfCategoryIdNotNull(registerRequestDto.getCategoryId());
        Board board = Board.createBoard(registerRequestDto, member, category, tmp);
        board.setPriceZeroIfMethodIsShare();
        boardRepository.save(board);
        boardPictureService.uploadPicturesIfExistAndUnderLimit(pictures, board);
        sendKeywordNotification(registerRequestDto, board);
        return board.getId();
    }

    @Override
    public BoardDetailResponseDto getBoardDetail(Long boardId, HttpServletRequest request) {
        Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);
        int like = boardLikeRepository.countByBoard(board);

        String ip = getClientIp(request);
        log.debug("IP: {}", ip);
        String userAgent = getUserAgent(request);
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
        // TODO 임시게시글이 없을 경우 null을 반환하지 않고 예외를 발생
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

    // TODO Util 클래스 분리
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader(HEADER_X_FORWARDED_FOR);
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    // TODO Util 클래스 분리
    private String getUserAgent(HttpServletRequest request) {
        return request.getHeader(HEADER_USER_AGENT);
    }

    // TODO BoardNotificationService 클래스로 분리?
    private void sendKeywordNotification(BoardRegisterRequestDto registerRequestDto, Board board) {
        Set<String> wordsNotDuplicated = getTitleAndDescriptionNotDuplicated(registerRequestDto);
        Set<Keyword> keywords = keywordRepository.findByNameIn(wordsNotDuplicated);
        BoardNotificationResponseDto notification = new BoardNotificationResponseDto(board);
        sendBoardNotificationResponseDto(keywords, notification);
    }

    private Set<String> getTitleAndDescriptionNotDuplicated(BoardRegisterRequestDto registerRequestDto) {
        String[] words = (registerRequestDto.getTitle() + " " + registerRequestDto.getDescription()).split(" ");
        return new HashSet<>(List.of(words));
    }

    private void sendBoardNotificationResponseDto(Set<Keyword> keywords, BoardNotificationResponseDto notification) {
        for (Keyword keyword : keywords) {
            notificationService.add(keyword.getMember().getAuthId(), NotificationType.NOTICE, notification);
        }
    }
}
