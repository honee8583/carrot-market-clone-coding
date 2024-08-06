package com.carrot.carrotmarketclonecoding.board.service.impl;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.domain.BoardPicture;
import com.carrot.carrotmarketclonecoding.board.domain.Category;
import com.carrot.carrotmarketclonecoding.board.domain.enums.Method;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.board.repository.BoardPictureRepository;
import com.carrot.carrotmarketclonecoding.board.repository.BoardRepository;
import com.carrot.carrotmarketclonecoding.board.repository.CategoryRepository;
import com.carrot.carrotmarketclonecoding.board.service.BoardService;
import com.carrot.carrotmarketclonecoding.common.exception.CategoryNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.file.service.impl.FileServiceImpl;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
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
    private final FileServiceImpl fileService;

    @Override
    public Long register(BoardRegisterRequestDto registerRequestDto, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        Category category = categoryRepository.findById(registerRequestDto.getCategoryId()).orElseThrow(CategoryNotFoundException::new);

        if (registerRequestDto.getMethod() == Method.SHARE) {
            registerRequestDto.setPrice(0);
        }

        Board board = boardRepository.save(Board.createBoard(registerRequestDto, member, category));

        if (registerRequestDto.getPictures() != null && registerRequestDto.getPictures().length > 0) {
            savePictures(registerRequestDto.getPictures(), board);
        }

        return board.getId();
    }

    private void savePictures(MultipartFile[] pictures, Board board) {
        for (MultipartFile file : pictures) {
            String pictureUrl = fileService.uploadImage(file);

            log.debug("pictureUrl: " + pictureUrl);

            boardPictureRepository.save(BoardPicture.builder()
                    .board(board)
                    .pictureUrl(pictureUrl)
                    .build());
        }
    }
}
