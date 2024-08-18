package com.carrot.carrotmarketclonecoding.board.service.impl;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.domain.BoardPicture;
import com.carrot.carrotmarketclonecoding.board.repository.BoardPictureRepository;
import com.carrot.carrotmarketclonecoding.common.exception.FileUploadLimitException;
import com.carrot.carrotmarketclonecoding.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BoardPictureService {
    private final BoardPictureRepository boardPictureRepository;
    private final FileService fileService;

    private static final int FILE_LIMIT_COUNT = 10;

    @Transactional
    public void uploadPicturesIfExistAndUnderLimit(MultipartFile[] pictures, Board board) {
        validatePicturesCountLimit(pictures);
        if (isUploadPicturesExist(pictures)) {
            uploadPictures(pictures, board);
        }
    }

    @Transactional
    public void deletePicturesIfExist(Long[] deletePictures) {
        if (isDeletePicturesExist(deletePictures)) {
            deleteBoardPictures(deletePictures);
        }
    }

    private boolean isDeletePicturesExist(Long[] deletePictures) {
        return deletePictures != null && deletePictures.length != 0;
    }

    private void deleteBoardPictures(Long[] deletePictures) {
        for (Long pictureId : deletePictures) {
            boardPictureRepository.deleteById(pictureId);
        }
    }

    private void validatePicturesCountLimit(MultipartFile[] pictures) {
        if (pictures != null && pictures.length > FILE_LIMIT_COUNT) {
            throw new FileUploadLimitException();
        }
    }

    private boolean isUploadPicturesExist(MultipartFile[] pictures) {
        return pictures != null && pictures.length > 0;
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
