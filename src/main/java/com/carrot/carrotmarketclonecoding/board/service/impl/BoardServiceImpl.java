package com.carrot.carrotmarketclonecoding.board.service.impl;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.domain.BoardPicture;
import com.carrot.carrotmarketclonecoding.board.domain.Category;
import com.carrot.carrotmarketclonecoding.board.domain.enums.Method;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
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
    public Long register(BoardRegisterRequestDto registerRequestDto, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        Category category = categoryRepository.findById(registerRequestDto.getCategoryId())
                .orElseThrow(CategoryNotFoundException::new);

        checkMethod(registerRequestDto);

        Board board = boardRepository.save(Board.createBoard(registerRequestDto, member, category));

        validatePictures(registerRequestDto.getPictures());

        if (registerRequestDto.getPictures() != null && registerRequestDto.getPictures().length > 0) {
            uploadPictures(registerRequestDto.getPictures(), board);
        }

        return board.getId();
    }

    @Override
    public BoardDetailResponseDto detail(Long boardId, Long memberId) {
        Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);
        List<BoardPicture> pictures = boardPictureRepository.findByBoard(board);
        int like = boardLikeRepository.countByBoard(board);

        if (visitService.increaseVisit(board.getId().toString(), memberId.toString())) {
            board.increaseVisit();
            boardRepository.save(board);
        }

        // TODO count chats

        return BoardDetailResponseDto.createBoardDetail(board, pictures, like);
    }

    private void checkMethod(BoardRegisterRequestDto registerRequestDto) {
        if (registerRequestDto.getMethod() == Method.SHARE) {
            registerRequestDto.setPrice(0);
        }
    }

    private void validatePictures(MultipartFile[] pictures) {
        if (pictures != null && pictures.length > FILE_LIMIT_COUNT) {
            throw new FileUploadLimitException();
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
}
