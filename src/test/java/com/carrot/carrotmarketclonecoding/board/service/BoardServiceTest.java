package com.carrot.carrotmarketclonecoding.board.service;

import static com.carrot.carrotmarketclonecoding.board.BoardTestDisplayNames.MESSAGE.*;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.domain.BoardPicture;
import com.carrot.carrotmarketclonecoding.board.domain.Category;
import com.carrot.carrotmarketclonecoding.board.domain.enums.Method;
import com.carrot.carrotmarketclonecoding.board.domain.enums.SearchOrder;
import com.carrot.carrotmarketclonecoding.board.domain.enums.Status;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardSearchRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardUpdateRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.MyBoardSearchRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardDetailResponseDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardSearchResponseDto;
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
import com.carrot.carrotmarketclonecoding.common.response.PageResponseDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Mock
    private SearchKeywordService searchKeywordService;

    @Nested
    @DisplayName(BOARD_REGISTER_SERVICE_TEST)
    class RegisterBoard {

        @Test
        @DisplayName(SUCCESS)
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
        @DisplayName(FAIL_FILE_COUNT_OVER_10)
        void fileUploadLimitExceeded() {
            // given
            MultipartFile[] files = createFiles(20);
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
        @DisplayName(FAIL_WRITER_NOT_FOUND)
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
        @DisplayName(FAIL_CATEGORY_NOT_FOUND)
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
    @DisplayName(BOARD_DETAIL_SERVICE_TEST)
    class BoardDetail {

        @Test
        @DisplayName(SUCCESS_INCLUDE_INCREASE_VISIT)
        void boardDetailSuccess() {
            // given
            Long boardId = 1L;
            Long memberId = 1L;
            String sessionId = "session:" + memberId;
            List<BoardPicture> mockPictures = Arrays.asList(
                    BoardPicture.builder().id(1L).build(),
                    BoardPicture.builder().id(2L).build());
            Board mockBoard = createMockBoard(boardId, memberId, mockPictures);


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
        @DisplayName(SUCCESS_NOT_INCREASE_VISIT_REVISIT_IN_24HOURS)
        void boardDetailSuccessVisitCountNotIncreased() {
            // given
            Long boardId = 1L;
            Long memberId = 1L;
            List<BoardPicture> mockPictures = Arrays.asList(
                    BoardPicture.builder().id(1L).build(),
                    BoardPicture.builder().id(2L).build());
            Board mockBoard = createMockBoard(boardId, memberId, mockPictures);

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
        @DisplayName(FAIL_BOARD_NOT_FOUND)
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
    }

    @Nested
    @DisplayName(BOARD_SEARCH_SERVICE_TEST)
    class SearchBoards {

        @Test
        @DisplayName(SUCCESS)
        void searchBoardsSuccess() {
            // given
            Long memberId = 1L;
            Member mockMember = Member.builder().id(memberId).build();
            List<BoardSearchResponseDto> boardSearchResponses = Arrays.asList(
                    new BoardSearchResponseDto(),
                    new BoardSearchResponseDto()
            );
            Pageable pageable = PageRequest.of(0, 10);
            Page<BoardSearchResponseDto> searchResult = new PageImpl<>(boardSearchResponses, pageable, 2);
            BoardSearchRequestDto searchRequestDto = BoardSearchRequestDto.builder()
                    .categoryId(1L)
                    .keyword("title")
                    .minPrice(0)
                    .maxPrice(20000)
                    .order(SearchOrder.NEWEST)
                    .build();

            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(mockMember));
            when(boardRepository.findAllByMemberAndSearchRequestDto(any(), any(), any())).thenReturn(searchResult);

            // when
            PageResponseDto<BoardSearchResponseDto> response = boardService.search(memberId, searchRequestDto, pageable);

            // then
            verify(searchKeywordService).addRecentSearchKeywords(memberId, searchRequestDto.getKeyword());
            verify(searchKeywordService).addSearchKeywordRank(searchRequestDto.getKeyword());
            assertThat(response.getContents().size()).isEqualTo(2);
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void searchBoardsFailMemberNotFound() {
            // given
            when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.search(1L, mock(BoardSearchRequestDto.class), mock(Pageable.class)))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName(BOARD_MY_DETAIL_SERVICE_TEST)
    class MyBoards {

        @Test
        @DisplayName(SUCCESS)
        void searchMyBoardsSuccess() {
            // given
            Long memberId = 1L;
            List<BoardSearchResponseDto> boardSearchResponses = Arrays.asList(
                    new BoardSearchResponseDto(),
                    new BoardSearchResponseDto()
            );
            Pageable pageable = PageRequest.of(0, 10);
            Page<BoardSearchResponseDto> searchResult = new PageImpl<>(boardSearchResponses, pageable, 2);

            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(mock(Member.class)));
            when(boardRepository.findAllByStatusOrHide(any(), any(), any())).thenReturn(searchResult);

            // when
            PageResponseDto<BoardSearchResponseDto> response = boardService.searchMyBoards(memberId, new MyBoardSearchRequestDto(), pageable);

            // then
            assertThat(response.getContents().size()).isEqualTo(2);
            assertThat(response.getNumberOfElements()).isEqualTo(2);
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void searchBoardsFailMemberNotFound() {
            // given
            when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.searchMyBoards(1L, mock(MyBoardSearchRequestDto.class), mock(Pageable.class)))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName(BOARD_UPDATE_SERVICE_TEST)
    class UpdateBoard {

        @Test
        @DisplayName(SUCCESS_DELETE_OLD_TMP_BOARDS)
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
        @DisplayName(SUCCESS_NOT_DELETE_OLD_TMP_BOARDS_IF_BOARD_NOT_TMP)
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
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void updateBoardFailMemberNotExists() {
            // given
            when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.update(createUpdateRequestDto(), 1L, 1L))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName(FAIL_BOARD_NOT_FOUND)
        void updateBoardFailBoardNotExists() {
            // given
            Long boardId = 1L;
            Long memberId = 1L;
            Member mockMember = Member.builder().id(memberId).build();
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(mock(Member.class)));
            when(boardRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.update(createUpdateRequestDto(), boardId, memberId))
                    .isInstanceOf(BoardNotFoundException.class)
                    .hasMessage(BOARD_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName(FAIL_MEMBER_IS_NOT_WRITER)
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
        @DisplayName(FAIL_CATEGORY_NOT_FOUND)
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
        @DisplayName(FAIL_NEW_PICTURES_COUNT_OVER_10)
        void updateBoardFailFileUploadLimit() {
            // given
            Long boardId = 1L;
            Long memberId = 1L;
            Long categoryId = 1L;
            Member mockMember = Member.builder().id(memberId).build();
            Category mockCategory = Category.builder().id(categoryId).build();
            Board mockBoard = Board.builder().id(boardId).member(mockMember).category(mockCategory).tmp(false).build();

            MultipartFile[] files = createFiles(20);
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
            return BoardUpdateRequestDto.builder()
                    .title("title")
                    .categoryId(1L)
                    .method(Method.SHARE)
                    .price(20000)
                    .suggest(false)
                    .description("description")
                    .place("place")
                    .removePictures(new Long[]{1L, 2L, 3L})
                    .newPictures(createFiles(10))
                    .build();
        }
    }

    @Nested
    @DisplayName(BOARD_DELETE_SERVICE_TEST)
    class DeleteBoard {

        @Test
        @DisplayName(SUCCESS)
        void deleteBoardSuccess() {
            // given
            Long boardId = 1L;
            Long memberId = 1L;
            Member mockMember = Member.builder().id(memberId).build();
            Board mockBoard = Board.builder().id(boardId).member(mockMember).build();

            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(mockMember));

            // when
            boardService.delete(boardId, memberId);

            // then
            verify(boardRepository).delete(mockBoard);
        }

        @Test
        @DisplayName(FAIL_BOARD_NOT_FOUND)
        void deleteBoardFailBoardNotFound() {
            // given
            Long boardId = 1L;
            Long memberId = 1L;

            when(boardRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.delete(boardId, memberId))
                    .isInstanceOf(BoardNotFoundException.class)
                    .hasMessage(BOARD_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void deleteBoardFailMemberNotFound() {
            // given
            Long boardId = 1L;
            Long memberId = 1L;
            Board mockBoard = Board.builder().id(boardId).build();

            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));
            when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.delete(boardId, memberId))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName(FAIL_MEMBER_IS_NOT_WRITER)
        void deleteBoardFailMemberIsNotWriter() {
            // given
            Long boardId = 1L;
            Long memberId = 1L;
            Member mockMember = Member.builder().id(memberId).build();
            Board mockBoard = Board.builder().id(boardId).member(Member.builder().id(2L).build()).build();

            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(mockMember));

            // when
            // then
            assertThatThrownBy(() -> boardService.delete(boardId, memberId))
                    .isInstanceOf(UnauthorizedAccessException.class)
                    .hasMessage(UNAUTHORIZED_ACCESS.getMessage());
        }
    }

    @Nested
    @DisplayName(BOARD_GET_TMP_SERVICE_TEST)
    class TmpBoardDetail {

        @Test
        @DisplayName(SUCCESS)
        void tmpBoardDetailSuccess() {
            // given
            Long memberId = 1L;
            Long boardId = 1L;
            List<BoardPicture> mockPictures = Arrays.asList(
                    BoardPicture.builder().id(1L).build(),
                    BoardPicture.builder().id(2L).build());
            Member mockMember = Member.builder().id(memberId).build();
            Board mockBoard = createMockBoard(boardId, memberId, mockPictures);

            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(mockMember));
            when(boardRepository.findFirstByMemberAndTmpIsTrueOrderByCreateDateDesc(any())).thenReturn(Optional.of(mockBoard));

            // when
            BoardDetailResponseDto boardDetail = boardService.tmpBoardDetail(memberId);

            // then
            assertThat(boardDetail.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName(SUCCESS_NO_TMP_BOARDS)
        void tmpBoardDetailSuccessNoTmpBoards() {
            // given
            Long memberId = 1L;
            Member mockMember = Member.builder().id(memberId).build();

            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(mockMember));
            when(boardRepository.findFirstByMemberAndTmpIsTrueOrderByCreateDateDesc(any())).thenReturn(Optional.empty());

            // when
            BoardDetailResponseDto boardDetail = boardService.tmpBoardDetail(memberId);

            // then
            assertThat(boardDetail).isNull();
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void tmpBoardDetailFailMemberNotFound() {
            // given
            Long memberId = 1L;
            when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.tmpBoardDetail(memberId))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }
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

    private Board createMockBoard(Long boardId, Long memberId, List<BoardPicture> boardPictures) {
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
                .boardPictures(boardPictures)
                .build();
    }
}