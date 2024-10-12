package com.carrot.carrotmarketclonecoding.board.service;

import static com.carrot.carrotmarketclonecoding.board.displayname.BoardTestDisplayNames.MESSAGE.*;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.domain.BoardPicture;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardNotificationResponseDto;
import com.carrot.carrotmarketclonecoding.category.domain.Category;
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
import com.carrot.carrotmarketclonecoding.category.repository.CategoryRepository;
import com.carrot.carrotmarketclonecoding.board.service.impl.BoardPictureService;
import com.carrot.carrotmarketclonecoding.board.service.impl.BoardServiceImpl;
import com.carrot.carrotmarketclonecoding.common.exception.BoardNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.CategoryNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.FileUploadLimitException;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    private VisitRedisService visitRedisService;

    @Mock
    private BoardPictureService boardPictureService;

    @Mock
    private SearchKeywordRedisService searchKeywordRedisService;

    @Mock
    private KeywordRepository keywordRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private HttpServletRequest request;

    @Nested
    @DisplayName(BOARD_REGISTER_SERVICE_TEST)
    class RegisterBoard {

        @Test
        @DisplayName(SUCCESS)
        void registerBoardSuccess() {
            // given
            Member mockMember = Member.builder()
                    .id(1L)
                    .authId(1111L)
                    .build();
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));

            Category mockCategory = Category.builder().id(1L).build();
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(mockCategory));

            Set<Keyword> keywords = new HashSet<>(Arrays.asList(
                    Keyword.builder()
                            .member(Member.builder().authId(2222L).build())
                            .name("keyword1")
                            .build(),
                    Keyword.builder()
                            .member(Member.builder().authId(3333L).build())
                            .name("keyword2")
                            .build()
            ));
            when(keywordRepository.findByNameIn(anySet())).thenReturn(keywords);

            // when
            BoardRegisterRequestDto boardRegisterRequestDto = createRegisterRequestDto();
            Long registerId = boardService.register(boardRegisterRequestDto, 1111L, false);

            // then
            ArgumentCaptor<Board> boardCaptor = ArgumentCaptor.forClass(Board.class);
            verify(boardRepository).save(boardCaptor.capture());
            Board capturedBoard = boardCaptor.getValue();
            assertThat(registerId).isEqualTo(capturedBoard.getId());

            verify(boardPictureService)
                    .uploadPicturesIfExistAndUnderLimit(eq(boardRegisterRequestDto.getPictures()), eq(capturedBoard));

            ArgumentCaptor<BoardNotificationResponseDto> notificationCaptor = ArgumentCaptor
                    .forClass(BoardNotificationResponseDto.class);
            verify(notificationService, times(2))
                    .add(anyLong(), any(NotificationType.class), notificationCaptor.capture());
            BoardNotificationResponseDto notification = notificationCaptor.getValue();
            assertThat(notification.getTitle()).isEqualTo(capturedBoard.getTitle());
            assertThat(notification.getPrice()).isEqualTo(capturedBoard.getPrice());
        }

        @Test
        @DisplayName(FAIL_FILE_COUNT_OVER_10)
        void fileUploadLimitExceeded() {
            // given
            Long memberId = 1L;
            Member mockMember = Member.builder().id(memberId).build();
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));

            Long categoryId = 1L;
            Category mockCategory = Category.builder().id(categoryId).build();
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(mockCategory));

            Board mockBoard = Board.builder().id(1L).build();
            when(boardRepository.save(any(Board.class))).thenReturn(mockBoard);

            // when
            doThrow(FileUploadLimitException.class).when(boardPictureService).uploadPicturesIfExistAndUnderLimit(any(), any());

            // then
            BoardRegisterRequestDto boardRegisterRequestDto = new BoardRegisterRequestDto();
            MultipartFile[] files = createFiles(20);
            boardRegisterRequestDto.setPictures(files);
            boardRegisterRequestDto.setCategoryId(categoryId);
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
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.register(createRegisterRequestDto(), memberId, false))
                    .isInstanceOf(CategoryNotFoundException.class)
                    .hasMessage(CATEGORY_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName(FAIL_CATEGORY_NOT_FOUND)
        void registerBoardCategoryNotFound() {
            // given
            Long memberId = 1L;
            when(memberRepository.findByAuthId(any())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.register(createRegisterRequestDto(), memberId, false))
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
            List<BoardPicture> mockPictures = createBoardPictures(2);
            Board mockBoard = createMockBoard(boardId, 1L, mockPictures);
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));
            when(boardLikeRepository.countByBoard(any())).thenReturn(10);
            when(visitRedisService.increaseVisit(anyString(), any(), any())).thenReturn(true);

            // when
            BoardDetailResponseDto result = boardService.detail(boardId, request);

            // then
            // todo
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
            List<BoardPicture> mockPictures = createBoardPictures(2);
            Board mockBoard = createMockBoard(1L, 1L, mockPictures);
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));
            when(boardLikeRepository.countByBoard(any())).thenReturn(10);
            when(visitRedisService.increaseVisit(anyString(), any(), any())).thenReturn(false);

            // when
            BoardDetailResponseDto result = boardService.detail(boardId, request);

            // then
            assertThat(result.getVisit()).isEqualTo(10);
        }

        @Test
        @DisplayName(FAIL_BOARD_NOT_FOUND)
        void boardDetailBoardNotFound() {
            // given
            when(boardRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.detail(1L, request))
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
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));

            List<BoardSearchResponseDto> boardSearchResponses = Arrays.asList(
                    new BoardSearchResponseDto(),
                    new BoardSearchResponseDto()
            );
            Pageable pageable = PageRequest.of(0, 10);
            Page<BoardSearchResponseDto> searchResult = new PageImpl<>(boardSearchResponses, pageable, 2);
            when(boardRepository.findAllBySearchRequestDto(any(), any())).thenReturn(searchResult);

            // when
            BoardSearchRequestDto searchRequestDto = BoardSearchRequestDto.builder()
                    .categoryId(1L)
                    .keyword("title")
                    .minPrice(0)
                    .maxPrice(20000)
                    .order(SearchOrder.NEWEST)
                    .build();
            PageResponseDto<BoardSearchResponseDto> response = boardService.search(memberId, searchRequestDto, pageable);

            // then
            verify(searchKeywordRedisService).addRecentSearchKeywords(memberId, searchRequestDto.getKeyword());
            verify(searchKeywordRedisService).addSearchKeywordRank(searchRequestDto.getKeyword());
            assertThat(response.getContents().size()).isEqualTo(2);
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void searchBoardsFailMemberNotFound() {
            // given
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());

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
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mock(Member.class)));

            List<BoardSearchResponseDto> boardSearchResponses = Arrays.asList(
                    new BoardSearchResponseDto(),
                    new BoardSearchResponseDto()
            );
            Pageable pageable = PageRequest.of(0, 10);
            Page<BoardSearchResponseDto> searchResult = new PageImpl<>(boardSearchResponses, pageable, 2);
            when(boardRepository.findAllByStatusOrHide(any(), any(), any())).thenReturn(searchResult);

            // when
            PageResponseDto<BoardSearchResponseDto> response = boardService.searchMyBoards(1L, new MyBoardSearchRequestDto(), pageable);

            // then
            assertThat(response.getContents().size()).isEqualTo(2);
            assertThat(response.getNumberOfElements()).isEqualTo(2);
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void searchBoardsFailMemberNotFound() {
            // given
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());

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
            Long memberId = 1L;
            Member mockMember = Member.builder().id(1L).build();
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));

            Long boardId = 1L;
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
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));

            Category mockCategory = Category.builder().id(2L).build();
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(mockCategory));

            // when
            BoardUpdateRequestDto updateRequestDto = createUpdateRequestDto(null);
            updateRequestDto.setMethod(Method.SHARE);
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
            Long memberId = 1L;
            Member mockMember = Member.builder().id(memberId).build();
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));

            Category mockCategory = Category.builder().id(1L).build();
            Long boardId = 1L;
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
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(mockCategory));

            // when
            BoardUpdateRequestDto updateRequestDto = createUpdateRequestDto(null);
            boardService.update(updateRequestDto, boardId, memberId);

            // then
            verify(boardRepository, times(0)).deleteAllByMemberAndTmpIsTrueAndIdIsNot(mockMember, boardId);
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void updateBoardFailMemberNotExists() {
            // given
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.update(createUpdateRequestDto(null), 1L, 1L))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName(FAIL_BOARD_NOT_FOUND)
        void updateBoardFailBoardNotExists() {
            // given
            Long memberId = 1L;
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(Member.builder().id(memberId).build()));
            when(boardRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.update(createUpdateRequestDto(null), 1L, memberId))
                    .isInstanceOf(BoardNotFoundException.class)
                    .hasMessage(BOARD_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName(FAIL_MEMBER_IS_NOT_WRITER)
        void updateBoardFailMemberIsNotWriter() {
            // given
            Long memberId = 1L;
            Member mockMember = Member.builder().id(memberId).build();
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));

            Long boardId = 1L;
            Board mockBoard = Board.builder()
                    .id(boardId)
                    .member(Member.builder().id(2L).build())
                    .build();
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));

            // when
            // then
            assertThatThrownBy(() -> boardService.update(createUpdateRequestDto(null), boardId, memberId))
                    .isInstanceOf(UnauthorizedAccessException.class)
                    .hasMessage(UNAUTHORIZED_ACCESS.getMessage());
        }

        @Test
        @DisplayName(FAIL_CATEGORY_NOT_FOUND)
        void updateBoardFailCategoryNotFound() {
            // given
            Long memberId = 1L;
            Member mockMember = Member.builder().id(memberId).build();
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));

            Long boardId = 1L;
            Board mockBoard = Board.builder()
                    .id(boardId)
                    .member(mockMember)
                    .tmp(false)
                    .build();
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.update(createUpdateRequestDto(null), boardId, memberId))
                    .isInstanceOf(CategoryNotFoundException.class)
                    .hasMessage(CATEGORY_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName(FAIL_NEW_PICTURES_COUNT_OVER_10)
        void updateBoardFailFileUploadLimit() {
            // given
            Long memberId = 1L;
            Member mockMember = Member.builder().id(memberId).build();
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));

            Long categoryId = 1L;
            Category mockCategory = Category.builder().id(categoryId).build();
            Long boardId = 1L;
            Board mockBoard = Board.builder()
                    .id(boardId)
                    .member(mockMember)
                    .category(mockCategory)
                    .tmp(false)
                    .build();
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(mockCategory));

            BoardUpdateRequestDto updateRequestDto = createUpdateRequestDto(createFiles(20));
            doThrow(FileUploadLimitException.class).when(boardPictureService).uploadPicturesIfExistAndUnderLimit(updateRequestDto.getNewPictures(), mockBoard);

            // when
            // then
            assertThatThrownBy(() -> boardService.update(updateRequestDto, boardId, memberId))
                    .isInstanceOf(FileUploadLimitException.class)
                    .hasMessage(FILE_UPLOAD_LIMIT.getMessage());
        }

        private BoardUpdateRequestDto createUpdateRequestDto(MultipartFile[] pictures) {
            return BoardUpdateRequestDto.builder()
                    .title("updated title")
                    .categoryId(2L)
                    .method(Method.SELL)
                    .price(20000)
                    .suggest(false)
                    .description("updated description")
                    .place("updated place")
                    .removePictures(new Long[]{1L, 2L, 3L})
                    .newPictures(pictures)
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
            Long memberId = 1L;
            Member mockMember = Member.builder().id(memberId).build();
            Long boardId = 1L;
            Board mockBoard = Board.builder().id(boardId).member(mockMember).build();
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));

            // when
            boardService.delete(boardId, memberId);

            // then
            verify(boardLikeRepository).deleteAllByBoardId(boardId);
            verify(boardPictureRepository).deleteAllByBoardId(boardId);
            verify(boardRepository).delete(mockBoard);
        }

        @Test
        @DisplayName(FAIL_BOARD_NOT_FOUND)
        void deleteBoardFailBoardNotFound() {
            // given
            when(boardRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.delete(1L, 1L))
                    .isInstanceOf(BoardNotFoundException.class)
                    .hasMessage(BOARD_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void deleteBoardFailMemberNotFound() {
            // given
            Long boardId = 1L;
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(Board.builder().id(boardId).build()));

            Long memberId = 1L;
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());

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
            Board mockBoard = Board.builder().id(boardId).member(Member.builder().id(2L).build()).build();
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));

            Long memberId = 1L;
            Member mockMember = Member.builder().id(memberId).build();
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));

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
            Member mockMember = Member.builder().id(memberId).build();
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));

            Long boardId = 1L;
            List<BoardPicture> mockPictures = createBoardPictures(2);
            Board mockBoard = createMockBoard(boardId, memberId, mockPictures);
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
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));
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
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.tmpBoardDetail(1L))
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

    private List<BoardPicture> createBoardPictures(int size) {
        List<BoardPicture> boardPictures = new ArrayList<>();
        for (long i = 0; i < size; i++) {
            boardPictures.add(BoardPicture.builder().id(i + 1).build());
        }
        return boardPictures;
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