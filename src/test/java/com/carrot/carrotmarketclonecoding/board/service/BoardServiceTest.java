package com.carrot.carrotmarketclonecoding.board.service;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.domain.BoardPicture;
import com.carrot.carrotmarketclonecoding.board.domain.Category;
import com.carrot.carrotmarketclonecoding.board.domain.enums.Method;
import com.carrot.carrotmarketclonecoding.board.domain.enums.Status;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardUpdateRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardDetailResponseDto;
import com.carrot.carrotmarketclonecoding.board.repository.BoardLikeRepository;
import com.carrot.carrotmarketclonecoding.board.repository.BoardPictureRepository;
import com.carrot.carrotmarketclonecoding.board.repository.BoardRepository;
import com.carrot.carrotmarketclonecoding.board.repository.CategoryRepository;
import com.carrot.carrotmarketclonecoding.board.service.impl.BoardPictureService;
import com.carrot.carrotmarketclonecoding.board.service.impl.BoardServiceImpl;
import com.carrot.carrotmarketclonecoding.common.exception.BoardNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.CategoryNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.FileUploadLimitException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.UnauthorizedAccessException;
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
    private VisitService visitService;

    @Mock
    private BoardPictureService boardPictureService;

    @Nested
    @DisplayName("게시글 작성 서비스 테스트")
    class RegisterBoard {

        @Test
        @DisplayName("성공")
        void registerBoardSuccess() {
            // given
            Long memberId = 1L;
            Long categoryId = 1L;
            Member mockMember = Member.builder().id(memberId).build();
            Category mockCategory = Category.builder().id(categoryId).build();

            BoardRegisterRequestDto boardRegisterRequestDto = createRegisterRequestDto();
            Board mockBoard = Board.builder()
                            .id(1L)
                            .title(boardRegisterRequestDto.getTitle())
                            .member(mockMember)
                            .category(mockCategory)
                            .method(boardRegisterRequestDto.getMethod())
                            .price(boardRegisterRequestDto.getPrice())
                            .suggest(boardRegisterRequestDto.getSuggest())
                            .description(boardRegisterRequestDto.getDescription())
                            .place(boardRegisterRequestDto.getPlace())
                            .tmp(false)
                            .build();

            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(mockMember));
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(mockCategory));
            when(boardRepository.save(any(Board.class))).thenReturn(mockBoard);

            // 저장해서 얻은 Board 객체의 정보를 가지고 사진을 업로드할때의 인자값과 일치하는지 확인
            ArgumentCaptor<Board> boardCaptor = ArgumentCaptor.forClass(Board.class);

            // when
            Long registerId = boardService.register(boardRegisterRequestDto, memberId, false);

            // then
            verify(boardRepository).save(boardCaptor.capture());
            Board capturedBoard = boardCaptor.getValue();

            verify(boardPictureService).uploadPicturesIfExistAndUnderLimit(eq(boardRegisterRequestDto.getPictures()), eq(capturedBoard));
            assertThat(registerId).isEqualTo(capturedBoard.getId());
        }

        @Test
        @DisplayName("실패 - 업로드 요청한 파일의 개수가 10개 초과")
        void fileUploadLimitExceeded() {
            // given
            MultipartFile[] files = createFilesOver10();
            BoardRegisterRequestDto boardRegisterRequestDto = new BoardRegisterRequestDto();
            boardRegisterRequestDto.setCategoryId(1L);
            boardRegisterRequestDto.setPictures(files);

            Long memberId = 1L;
            Long categoryId = 1L;
            Long boardId = 1L;
            Member mockMember = Member.builder().id(memberId).build();
            Category mockCategory = Category.builder().id(categoryId).build();
            Board mockBoard = Board.builder().id(boardId).build();

            // when
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(mockMember));
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(mockCategory));
            when(boardRepository.save(any(Board.class))).thenReturn(mockBoard);
            doThrow(FileUploadLimitException.class).when(boardPictureService).uploadPicturesIfExistAndUnderLimit(any(), any());

            // then
            assertThatThrownBy(() -> boardService.register(boardRegisterRequestDto, memberId, false))
                    .isInstanceOf(FileUploadLimitException.class)
                    .hasMessage(FILE_UPLOAD_LIMIT.getMessage());
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
            assertThatThrownBy(() -> boardService.register(boardRegisterRequestDto, memberId, false))
                    .isInstanceOf(CategoryNotFoundException.class)
                    .hasMessage(CATEGORY_NOT_FOUND.getMessage());
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
            assertThatThrownBy(() -> boardService.register(boardRegisterRequestDto, memberId, false))
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
            String sessionId = "session:" + memberId;
            Board mockBoard = createMockBoard(boardId, memberId);

            List<BoardPicture> mockPictures = Arrays.asList(
                    BoardPicture.builder().id(1L).build(),
                    BoardPicture.builder().id(2L).build());

            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));
            when(boardPictureRepository.findByBoard(any())).thenReturn(mockPictures);
            when(boardLikeRepository.countByBoard(any())).thenReturn(10);
            when(visitService.increaseVisit(anyString(), anyString())).thenReturn(true);

            // when
            BoardDetailResponseDto result = boardService.detail(boardId, sessionId);

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
            BoardDetailResponseDto result = boardService.detail(boardId, "sessionId:" + memberId);

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
            assertThatThrownBy(() -> boardService.detail(boardId, "sessionId:" + memberId))
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

    @Nested
    @DisplayName("게시글 수정 서비스 테스트")
    class UpdateBoard {

        @Test
        @DisplayName("성공 - 임시저장한 게시글을 수정한경우 이전 임시저장게시글 모두 삭제")
        void updateBoardWithDeleteTmpBoardsSuccess() {
            // given
            Long boardId = 1L;
            Long memberId = 1L;
            Member mockMember = Member.builder().id(memberId).build();
            Category mockCategory = Category.builder().id(2L).build();
            Board mockBoard = Board.builder()
                    .id(boardId)
                    .member(mockMember)
                    .category(Category.builder().id(1L).build())
                    .method(Method.SHARE)
                    .price(10000)
                    .suggest(true)
                    .description("description")
                    .place("place")
                    .tmp(true)
                    .build();

            BoardUpdateRequestDto updateRequestDto = createUpdateRequestDto();
            updateRequestDto.setCategoryId(2L);
            updateRequestDto.setMethod(Method.SHARE);
            updateRequestDto.setSuggest(false);
            updateRequestDto.setDescription("updated description");
            updateRequestDto.setPlace("updated place");

            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(mockMember));
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(mockCategory));

            // when
            boardService.update(updateRequestDto, boardId, memberId);

            // then
            verify(boardRepository).deleteAllByMemberAndTmpIsTrueAndIdIsNot(mockMember, boardId);
            verify(boardPictureService).deletePicturesIfExist(updateRequestDto.getRemovePictures());
            verify(boardPictureService).uploadPicturesIfExistAndUnderLimit(updateRequestDto.getNewPictures(), mockBoard);
            assertThat(mockBoard.getPrice()).isEqualTo(0);
            assertThat(mockBoard.getCategory().getId()).isEqualTo(2L);
            assertThat(mockBoard.getSuggest()).isEqualTo(false);
            assertThat(mockBoard.getDescription()).isEqualTo("updated description");
            assertThat(mockBoard.getPlace()).isEqualTo("updated place");
        }

        @Test
        @DisplayName("성공 - 임시저장한 게시글이 아닐경우 이전의 임시저장한 게시글을 삭제x")
        void updateBoardSuccess() {
            // given
            Long boardId = 1L;
            Long memberId = 1L;
            Long categoryId = 1L;
            Member mockMember = Member.builder().id(memberId).build();
            Category mockCategory = Category.builder().id(categoryId).build();
            Board mockBoard = Board.builder()
                    .id(boardId)
                    .member(mockMember)
                    .category(mockCategory)
                    .method(Method.SELL)
                    .price(10000)
                    .suggest(true)
                    .description("description")
                    .place("place")
                    .tmp(false)
                    .build();

            BoardUpdateRequestDto updateRequestDto = createUpdateRequestDto();

            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(mockMember));
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(mockCategory));

            // when
            boardService.update(updateRequestDto, boardId, memberId);

            // then
            verify(boardRepository, times(0)).deleteAllByMemberAndTmpIsTrueAndIdIsNot(mockMember, boardId);
        }

        @Test
        @DisplayName("실패 - 사용자가 존재하지 않음")
        void updateBoardFailMemberNotExists() {
            // given
            Long boardId = 1L;
            Long memberId = 1L;
            when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.update(createUpdateRequestDto(), boardId, memberId))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("실패 - 게시글이 존재하지 않음")
        void updateBoardFailBoardNotExists() {
            // given
            Long boardId = 1L;
            Long memberId = 1L;
            Member mockMember = Member.builder().id(memberId).build();
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(mockMember));
            when(boardRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.update(createUpdateRequestDto(), boardId, memberId))
                    .isInstanceOf(BoardNotFoundException.class)
                    .hasMessage(BOARD_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("실패 - 작성자와 사용자가 일치하지 않음")
        void updateBoardFailMemberIsNotWriter() {
            // given
            Long boardId = 1L;
            Long memberId = 1L;
            Member mockMember = Member.builder().id(memberId).build();
            Board mockBoard = Board.builder()
                    .id(boardId)
                    .member(Member.builder().id(2L).build())
                    .build();
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(mockMember));
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));

            // when
            // then
            assertThatThrownBy(() -> boardService.update(createUpdateRequestDto(), boardId, memberId))
                    .isInstanceOf(UnauthorizedAccessException.class)
                    .hasMessage(UNAUTHORIZED_ACCESS.getMessage());
        }

        @Test
        @DisplayName("실패 - 카테고리가 존재하지 않음")
        void updateBoardFailCategoryNotFound() {
            // given
            Long boardId = 1L;
            Long memberId = 1L;
            Member mockMember = Member.builder().id(memberId).build();
            Board mockBoard = Board.builder().id(boardId).member(mockMember).tmp(false).build();

            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(mockMember));
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.update(createUpdateRequestDto(), boardId, memberId))
                    .isInstanceOf(CategoryNotFoundException.class)
                    .hasMessage(CATEGORY_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("실패 - 새로 첨부하는 사진의 개수가 10개를 넘음")
        void updateBoardFailFileUploadLimit() {
            // given
            Long boardId = 1L;
            Long memberId = 1L;
            Long categoryId = 1L;
            Member mockMember = Member.builder().id(memberId).build();
            Category mockCategory = Category.builder().id(categoryId).build();
            Board mockBoard = Board.builder().id(boardId).member(mockMember).category(mockCategory).tmp(false).build();

            MultipartFile[] files = createFilesOver10();
            BoardUpdateRequestDto updateRequestDto = new BoardUpdateRequestDto();
            updateRequestDto.setNewPictures(files);
            updateRequestDto.setCategoryId(categoryId);

            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(mockMember));
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(mockCategory));
            doThrow(FileUploadLimitException.class).when(boardPictureService).uploadPicturesIfExistAndUnderLimit(updateRequestDto.getNewPictures(), mockBoard);

            // when
            // then
            assertThatThrownBy(() -> boardService.update(updateRequestDto, boardId, memberId))
                    .isInstanceOf(FileUploadLimitException.class)
                    .hasMessage(FILE_UPLOAD_LIMIT.getMessage());
        }

        private BoardUpdateRequestDto createUpdateRequestDto() {
            MultipartFile[] newPictures = {
                    new MockMultipartFile(
                            "pictures",
                            "test1.jpg",
                            "image/jpeg",
                            "test data".getBytes())
            };

            return BoardUpdateRequestDto.builder()
                    .title("title")
                    .categoryId(1L)
                    .method(Method.SHARE)
                    .price(20000)
                    .suggest(false)
                    .description("description")
                    .place("place")
                    .removePictures(new Long[]{1L, 2L, 3L})
                    .newPictures(newPictures)
                    .build();
        }
    }

    private MultipartFile[] createFilesOver10() {
        return IntStream.range(0, 20)
                .mapToObj(i -> new MockMultipartFile(
                        "file" + i,
                        "file" + i + ".png",
                        "text/png",
                        ("Picture" + i).getBytes()
                ))
                .toArray(MultipartFile[]::new);
    }
}