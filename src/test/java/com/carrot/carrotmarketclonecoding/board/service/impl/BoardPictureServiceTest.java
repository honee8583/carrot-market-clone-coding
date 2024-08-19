package com.carrot.carrotmarketclonecoding.board.service.impl;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.domain.BoardPicture;
import com.carrot.carrotmarketclonecoding.board.repository.BoardPictureRepository;
import com.carrot.carrotmarketclonecoding.common.exception.FileUploadLimitException;
import com.carrot.carrotmarketclonecoding.file.service.impl.FileServiceImpl;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(SpringExtension.class)
class BoardPictureServiceTest {

    @InjectMocks
    private BoardPictureService boardPictureService;

    @Mock
    private BoardPictureRepository boardPictureRepository;

    @Mock
    private FileServiceImpl fileService;

    @Test
    @DisplayName("첨부파일이 존재하고 10개를 넘지않을경우 성공")
    void uploadPicturesUnderLimit() {
        // given
        Board mockBoard = Board.builder().id(1L).build();
        MultipartFile[] files = createFiles(10);

        // when
        boardPictureService.uploadPicturesIfExistAndUnderLimit(files, mockBoard);

        // then
        verify(fileService, times(10)).uploadImage(any(MultipartFile.class));
        verify(boardPictureRepository, times(10)).save(any(BoardPicture.class));
    }

    @Test
    @DisplayName("첨부파일이 존재하고 10개를 넘을경우 실패")
    void uploadPicturesIfExistAndOverLimit() {
        // given
        Board mockBoard = Board.builder().id(1L).build();
        MultipartFile[] files = createFiles(20);

        // when
        // then
        assertThatThrownBy(() -> boardPictureService.uploadPicturesIfExistAndUnderLimit(files, mockBoard))
                .isInstanceOf(FileUploadLimitException.class)
                .hasMessage(FILE_UPLOAD_LIMIT.getMessage());
    }

    private MultipartFile[] createFiles(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> new MockMultipartFile(
                        "file" + i,
                        "file" + i + ".png",
                        "text/png",
                        ("Picture" + i).getBytes()
                ))
                .toArray(MultipartFile[]::new);
    }
}