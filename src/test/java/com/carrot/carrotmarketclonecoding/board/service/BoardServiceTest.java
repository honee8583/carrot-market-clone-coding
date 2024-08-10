package com.carrot.carrotmarketclonecoding.board.service;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.BOARD_NOT_FOUND;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.FILE_UPLOAD_LIMIT;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.MEMBER_NOT_FOUND;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.domain.BoardPicture;
import com.carrot.carrotmarketclonecoding.board.domain.Category;
import com.carrot.carrotmarketclonecoding.board.domain.enums.Method;
import com.carrot.carrotmarketclonecoding.board.domain.enums.Status;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardDetailResponseDto;
import com.carrot.carrotmarketclonecoding.board.repository.BoardLikeRepository;
import com.carrot.carrotmarketclonecoding.board.repository.BoardPictureRepository;
import com.carrot.carrotmarketclonecoding.board.repository.BoardRepository;
import com.carrot.carrotmarketclonecoding.board.repository.CategoryRepository;
import com.carrot.carrotmarketclonecoding.board.service.impl.BoardServiceImpl;
import com.carrot.carrotmarketclonecoding.common.exception.BoardNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.CategoryNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.FileUploadLimitException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.response.FailedMessage;
import com.carrot.carrotmarketclonecoding.file.service.impl.FileServiceImpl;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    private BoardLikeRepository boardLikeRepository;

    @Mock
    private FileServiceImpl fileService;

    @Mock
    private VisitService visitService;

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
            Member mockMember = Member.builder().id(memberId).build();
            Category mockCategory = Category.builder().id(categoryId).build();
            Board mockBoard = Board.builder().id(boardId).build();
            String mockPictureUrl = "https://test-bucket/test1.jpg";
            BoardPicture mockBoardPicture = BoardPicture.builder().id(1L).pictureUrl(mockPictureUrl).build();
            BoardRegisterRequestDto boardRegisterRequestDto = createRegisterRequestDto();

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
        @DisplayName("성공 - 나눔게시판의 경우 가격은 0으로 설정")
        void registerShareBoard() {
            // given
            Long memberId = 1L;
            Long categoryId = 1L;
            Long boardId = 1L;
            Member mockMember = Member.builder().id(memberId).build();
            Category mockCategory = Category.builder().id(categoryId).build();
            Board mockBoard = Board.builder().id(boardId).build();
            BoardRegisterRequestDto boardRegisterRequestDto = createRegisterRequestDto();
            boardRegisterRequestDto.setMethod(Method.SHARE);

            ArgumentCaptor<Board> captor = ArgumentCaptor.forClass(Board.class);

            when(memberRepository.findById(any())).thenReturn(Optional.of(mockMember));
            when(categoryRepository.findById(any())).thenReturn(Optional.of(mockCategory));
            when(boardRepository.save(captor.capture())).thenReturn(mockBoard);

            // when
            Long registeredBoardId = boardService.register(boardRegisterRequestDto, memberId);

            // then
            Board capturedBoard = captor.getValue();
            assertThat(capturedBoard.getPrice()).isEqualTo(0);
            assertThat(registeredBoardId).isEqualTo(boardId);
        }

        @Test
        @DisplayName("실패 - 업로드 요청한 파일의 개수가 10개 초과")
        void fileUploadLimitExceeded() {
            // given
            MultipartFile[] files = IntStream.range(0, 11)
                    .mapToObj(i -> new MockMultipartFile(
                            "file" + i,
                            "file" + i + ".png",
                            "text/png",
                            ("Picture" + i).getBytes()
                    ))
                    .toArray(MultipartFile[]::new);
            BoardRegisterRequestDto boardRegisterRequestDto = createRegisterRequestDto();
            boardRegisterRequestDto.setPictures(files);

            Long memberId = 1L;
            Long categoryId = 1L;
            Member mockMember = Member.builder().id(memberId).build();
            Category mockCategory = Category.builder().id(categoryId).build();

            // when
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(mockMember));
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(mockCategory));

            // then
            assertThatThrownBy(() -> boardService.register(boardRegisterRequestDto, memberId))
                    .hasMessage(FILE_UPLOAD_LIMIT.getMessage())
                    .isInstanceOf(FileUploadLimitException.class);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 작성자")
        void registerBoardMemberNotFound() {
            // given
            Long memberId = 1L;
            Member mockMember = Member.builder().id(memberId).build();
            BoardRegisterRequestDto boardRegisterRequestDto = createRegisterRequestDto();

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

    @Nested
    @DisplayName("게시글 조회 서비스 테스트")
    class BoardDetail {

        @Test
        @DisplayName("성공 - 조회수 증가 포함")
        void boardDetailSuccess() {
            // given
            Long boardId = 1L;
            Long memberId = 1L;
            Board mockBoard = createMockBoard(boardId, memberId);

            List<BoardPicture> mockPictures = Arrays.asList(
                    BoardPicture.builder().id(1L).build(),
                    BoardPicture.builder().id(2L).build());

            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));
            when(boardPictureRepository.findByBoard(any())).thenReturn(mockPictures);
            when(boardLikeRepository.countByBoard(any())).thenReturn(10);
            when(visitService.increaseVisit(anyString(), anyString())).thenReturn(true);

            // when
            BoardDetailResponseDto result = boardService.detail(boardId, memberId);

            // then
            assertThat(result.getId()).isEqualTo(boardId);
            assertThat(result.getTitle()).isEqualTo("title");
            assertThat(result.getWriter()).isEqualTo("member");
            assertThat(result.getCategory()).isEqualTo("category");
            assertThat(result.getPrice()).isEqualTo(20000);
            assertThat(result.getMethod()).isEqualTo(Method.SELL);
            assertThat(result.getSuggest()).isEqualTo(false);
            assertThat(result.getDescription()).isEqualTo("description");
            assertThat(result.getPlace()).isEqualTo("place");
            assertThat(result.getVisit()).isEqualTo(11);
            assertThat(result.getStatus()).isEqualTo(Status.SELL);
        }

        @Test
        @DisplayName("성공 - 24시간내에 재조회할경우 조회수 증가 x")
        void boardDetailSuccessVisitCountNotIncreased() {
            // given
            Long boardId = 1L;
            Long memberId = 1L;
            Board mockBoard = createMockBoard(boardId, memberId);

            List<BoardPicture> mockPictures = Arrays.asList(
                    BoardPicture.builder().id(1L).build(),
                    BoardPicture.builder().id(2L).build());

            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));
            when(boardPictureRepository.findByBoard(any())).thenReturn(mockPictures);
            when(boardLikeRepository.countByBoard(any())).thenReturn(10);
            when(visitService.increaseVisit(anyString(), anyString())).thenReturn(false);

            // when
            BoardDetailResponseDto result = boardService.detail(boardId, memberId);

            // then
            assertThat(result.getVisit()).isEqualTo(10);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 게시판")
        void boardDetailBoardNotFound() {
            // given
            Long boardId = 1L;
            Long memberId = 1L;
            when(boardRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.detail(boardId, memberId))
                    .isInstanceOf(BoardNotFoundException.class)
                    .hasMessage(BOARD_NOT_FOUND.getMessage());
        }

        private Board createMockBoard(Long boardId, Long memberId) {
            return Board.builder()
                    .id(boardId)
                    .title("title")
                    .member(Member.builder().id(memberId).nickname("member").build())
                    .category(Category.builder().id(1L).name("category").build())
                    .method(Method.SELL)
                    .price(20000)
                    .suggest(false)
                    .description("description")
                    .place("place")
                    .visit(10)
                    .status(Status.SELL)
                    .tmp(false)
                    .build();
        }
    }
}