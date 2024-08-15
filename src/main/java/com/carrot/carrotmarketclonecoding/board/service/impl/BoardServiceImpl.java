package com.carrot.carrotmarketclonecoding.board.service.impl;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.domain.BoardPicture;
import com.carrot.carrotmarketclonecoding.board.domain.Category;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardDetailResponseDto;
import com.carrot.carrotmarketclonecoding.board.repository.BoardLikeRepository;
import com.carrot.carrotmarketclonecoding.board.repository.BoardPictureRepository;
import com.carrot.carrotmarketclonecoding.board.repository.BoardRepository;
import com.carrot.carrotmarketclonecoding.board.repository.CategoryRepository;
import com.carrot.carrotmarketclonecoding.board.service.BoardService;
import com.carrot.carrotmarketclonecoding.board.service.VisitService;
import com.carrot.carrotmarketclonecoding.common.exception.BoardNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.CategoryNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.FileUploadLimitException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.file.service.impl.FileServiceImpl;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final BoardPictureRepository boardPictureRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final FileServiceImpl fileService;
    private final VisitService visitService;

    private final int FILE_LIMIT_COUNT = 10;

    @Override
    public Long register(BoardRegisterRequestDto registerRequestDto, Long memberId, boolean tmp) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        Category category = findCategoryIfCategoryIdNotNull(registerRequestDto.getCategoryId());
        registerRequestDto.setPriceZeroIfMethodIsShare();
        Board board = boardRepository.save(Board.createBoard(registerRequestDto, member, category, tmp));

        uploadPicturesIfExistAndUnderLimit(registerRequestDto.getPictures(), board);

        return board.getId();
    }

    @Override
    @Transactional
    public BoardDetailResponseDto detail(Long boardId, String sessionId) {
        Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);
        List<BoardPicture> pictures = boardPictureRepository.findByBoard(board);
        int like = boardLikeRepository.countByBoard(board);

        if (visitService.increaseVisit(board.getId().toString(), sessionId)) {
            board.increaseVisit();
        }

        // TODO count chats

        return BoardDetailResponseDto.createBoardDetail(board, pictures, like);
    }

    private void validatePicturesCountLimit(MultipartFile[] pictures) {
        if (pictures != null && pictures.length > FILE_LIMIT_COUNT) {
            throw new FileUploadLimitException();
        }
    }

    private boolean isPicturesExist(MultipartFile[] pictures) {
        return pictures != null && pictures.length > 0;
    }

    private void uploadPicturesIfExistAndUnderLimit(MultipartFile[] pictures, Board board) {
        validatePicturesCountLimit(pictures);
        if (isPicturesExist(pictures)) {
            uploadPictures(pictures, board);
        }
    }

    private void uploadPictures(MultipartFile[] pictures, Board board) {
        for (MultipartFile file : pictures) {
            String pictureUrl = fileService.uploadImage(file);
            boardPictureRepository.save(BoardPicture.builder()
                    .board(board)
                    .pictureUrl(pictureUrl)
                    .build());
        }
    }

    private Category findCategoryIfCategoryIdNotNull(Long categoryId) {
        if (categoryId != null && categoryId > 0) {
            return categoryRepository.findById(categoryId).orElseThrow(CategoryNotFoundException::new);
        }
        return null;
    }
}
