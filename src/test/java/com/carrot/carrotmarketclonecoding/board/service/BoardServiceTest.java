package com.carrot.carrotmarketclonecoding.board.service;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.MEMBER_NOT_FOUND;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.domain.BoardPicture;
import com.carrot.carrotmarketclonecoding.board.domain.Category;
import com.carrot.carrotmarketclonecoding.board.domain.enums.Method;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.board.repository.BoardPictureRepository;
import com.carrot.carrotmarketclonecoding.board.repository.BoardRepository;
import com.carrot.carrotmarketclonecoding.board.repository.CategoryRepository;
import com.carrot.carrotmarketclonecoding.board.service.impl.BoardServiceImpl;
import com.carrot.carrotmarketclonecoding.common.exception.CategoryNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.response.FailedMessage;
import com.carrot.carrotmarketclonecoding.file.service.impl.FileServiceImpl;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @InjectMocks
    private BoardServiceImpl boardService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BoardPictureRepository boardPictureRepository;

    @Mock
    private FileServiceImpl fileService;

    @Nested
    @DisplayName("게시글 작성 서비스 테스트")
    class RegisterBoard {

        @Test
        @DisplayName("성공")
        void registerBoard() {
            // given
            Long memberId = 1L;
            Long categoryId = 1L;
            Long boardId = 1L;
            BoardRegisterRequestDto boardRegisterRequestDto = createRegisterRequestDto();

            Member mockMember = Member.builder().id(memberId).build();
            Category mockCategory = Category.builder().id(categoryId).build();
            Board mockBoard = Board.builder().id(boardId).build();
            String mockPictureUrl = "https://test-bucket/test1.jpg";
            BoardPicture mockBoardPicture = BoardPicture.builder().id(1L).pictureUrl(mockPictureUrl).build();

            when(memberRepository.findById(any())).thenReturn(Optional.of(mockMember));
            when(categoryRepository.findById(any())).thenReturn(Optional.of(mockCategory));
            when(boardRepository.save(any())).thenReturn(mockBoard);
            when(fileService.uploadImage(any())).thenReturn(mockPictureUrl);
            when(boardPictureRepository.save(any())).thenReturn(mockBoardPicture);

            // when
            Long registeredBoardId = boardService.register(boardRegisterRequestDto, memberId);

            // then
            assertThat(registeredBoardId).isEqualTo(boardId);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 작성자")
        void registerBoardMemberNotFound() {
            // given
            Long memberId = 1L;
            BoardRegisterRequestDto boardRegisterRequestDto = createRegisterRequestDto();

            Member mockMember = Member.builder().id(memberId).build();

            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(mockMember));
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.register(boardRegisterRequestDto, memberId))
                    .isInstanceOf(CategoryNotFoundException.class)
                    .hasMessage(FailedMessage.CATEGORY_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 카테고리")
        void registerBoardCategoryNotFound() {
            // given
            Long memberId = 1L;
            BoardRegisterRequestDto boardRegisterRequestDto = createRegisterRequestDto();

            when(memberRepository.findById(any())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.register(boardRegisterRequestDto, memberId))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }

        private BoardRegisterRequestDto createRegisterRequestDto() {
            MultipartFile[] pictures = {
                    new MockMultipartFile(
                            "pictures",
                            "test1.jpg",
                            "image/jpeg",
                            "test data".getBytes())
            };

            return BoardRegisterRequestDto.builder()
                    .pictures(pictures)
                    .title("title")
                    .categoryId(1L)
                    .method(Method.SELL)
                    .price(200000)
                    .suggest(true)
                    .description("description")
                    .place("place")
                    .tmp(false)
                    .build();
        }
    }
}