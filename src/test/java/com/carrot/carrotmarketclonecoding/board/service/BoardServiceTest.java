package com.carrot.carrotmarketclonecoding.board.service;

import static com.carrot.carrotmarketclonecoding.board.displayname.BoardTestDisplayNames.MESSAGE.*;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.carrot.carrotmarketclonecoding.board.domain.Board;
import com.carrot.carrotmarketclonecoding.board.domain.BoardPicture;
import com.carrot.carrotmarketclonecoding.board.helper.board.BoardDtoFactory;
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
import com.carrot.carrotmarketclonecoding.chat.domain.ChatRoom;
import com.carrot.carrotmarketclonecoding.chat.repository.ChatRoomRepository;
import com.carrot.carrotmarketclonecoding.common.exception.BoardNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.CategoryNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.FileUploadLimitException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.TmpBoardNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.UnauthorizedAccessException;
import com.carrot.carrotmarketclonecoding.common.response.PageResponseDto;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private VisitRedisService visitRedisService;

    @Mock
    private BoardPictureService boardPictureService;

    @Mock
    private SearchKeywordRedisService searchKeywordRedisService;

    @Mock
    private BoardNotificationService boardNotificationService;

    @Mock
    private HttpServletRequest request;

    @Spy
    private BoardDtoFactory dtoFactory;

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

            // when
            BoardRegisterRequestDto boardRegisterRequestDto = dtoFactory.createRegisterRequestDto();
            MultipartFile[] pictures = dtoFactory.createFiles(2);
            Long registerId = boardService.register(1111L, boardRegisterRequestDto, pictures, false);

            // then
            ArgumentCaptor<Board> boardCaptor = ArgumentCaptor.forClass(Board.class);
            verify(boardRepository).save(boardCaptor.capture());
            Board capturedBoard = boardCaptor.getValue();
            assertThat(registerId).isEqualTo(capturedBoard.getId());

            verify(boardPictureService)
                    .uploadPicturesIfExistAndUnderLimit(eq(pictures), eq(capturedBoard));
            verify(boardNotificationService, times(1))
                    .sendKeywordNotification(boardRegisterRequestDto, capturedBoard);
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
            boardRegisterRequestDto.setCategoryId(categoryId);
            assertThatThrownBy(() -> boardService.register(memberId, boardRegisterRequestDto, dtoFactory.createFiles(20), false))
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
            assertThatThrownBy(() -> boardService.register(memberId, dtoFactory.createRegisterRequestDto(), dtoFactory.createFiles(2), false))
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
            assertThatThrownBy(() -> boardService.register(memberId, dtoFactory.createRegisterRequestDto(), dtoFactory.createFiles(2), false))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName(BOARD_DETAIL_SERVICE_TEST)
    class GetBoardDetail {

        @Test
        @DisplayName(SUCCESS_INCLUDE_INCREASE_VISIT)
        void boardDetailSuccess() {
            // given
            Long boardId = 1L;
            Member mockMember = Member.builder().id(1L).nickname("member").build();
            Category mockCategory = Category.builder().id(1L).name("category").build();
            List<BoardPicture> mockPictures = dtoFactory.createBoardPictures(2);
            Board mockBoard = dtoFactory.createMockBoard(boardId, mockMember, mockCategory, mockPictures);
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));
            when(boardLikeRepository.countByBoard(any())).thenReturn(10);
            when(visitRedisService.increaseVisit(anyString(), any(), any())).thenReturn(true);

            List<ChatRoom> chatRooms = Arrays.asList(ChatRoom.builder()
                    .receiver(mockMember)
                    .board(mockBoard)
                    .build());
            when(chatRoomRepository.findByBoard(any(Board.class))).thenReturn(chatRooms);

            // when
            BoardDetailResponseDto result = boardService.getBoardDetail(boardId, request);

            // then
            assertBoardDetail(result, boardId);
        }

        private void assertBoardDetail(BoardDetailResponseDto boardDetail, Long boardId) {
            assertThat(boardDetail.getId()).isEqualTo(boardId);
            assertThat(boardDetail.getTitle()).isEqualTo("title");
            assertThat(boardDetail.getWriter()).isEqualTo("member");
            assertThat(boardDetail.getCategory()).isEqualTo("category");
            assertThat(boardDetail.getPrice()).isEqualTo(20000);
            assertThat(boardDetail.getMethod()).isEqualTo(Method.SELL);
            assertThat(boardDetail.getSuggest()).isEqualTo(false);
            assertThat(boardDetail.getDescription()).isEqualTo("description");
            assertThat(boardDetail.getPlace()).isEqualTo("place");
            assertThat(boardDetail.getVisit()).isEqualTo(11);
            assertThat(boardDetail.getStatus()).isEqualTo(Status.SELL);
        }

        @Test
        @DisplayName(SUCCESS_NOT_INCREASE_VISIT_REVISIT_IN_24HOURS)
        void boardDetailSuccessVisitCountNotIncreased() {
            // given
            Long boardId = 1L;
            Member mockMember = Member.builder().nickname("member").build();
            Category mockCategory = Category.builder().name("category").build();
            List<BoardPicture> mockPictures = dtoFactory.createBoardPictures(2);
            Board mockBoard = dtoFactory.createMockBoard(boardId, mockMember, mockCategory, mockPictures);
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));
            when(boardLikeRepository.countByBoard(any())).thenReturn(10);
            when(visitRedisService.increaseVisit(anyString(), any(), any())).thenReturn(false);

            // when
            BoardDetailResponseDto result = boardService.getBoardDetail(boardId, request);

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
            assertThatThrownBy(() -> boardService.getBoardDetail(1L, request))
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

            Pageable pageable = PageRequest.of(0, 10);
            Page<BoardSearchResponseDto> searchResult = new PageImpl<>(dtoFactory.createBoardSearchResponseDtos(2), pageable, 2);
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
    class SearchMyBoards {

        @Test
        @DisplayName(SUCCESS)
        void searchMyBoardsSuccess() {
            // given
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mock(Member.class)));

            List<BoardSearchResponseDto> boardSearchResponses = dtoFactory.createBoardSearchResponseDtos(2);
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
            Member mockMember = Member.builder().id(1L).authId(1111L).build();
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));

            Long boardId = 1L;
            Category mockCategory = Category.builder().id(2L).build();
            Board mockBoard = Board.builder()
                    .id(boardId)
                    .member(mockMember)
                    .category(mockCategory)
                    .method(Method.SHARE)
                    .price(10000)
                    .suggest(true)
                    .description("description")
                    .place("place")
                    .tmp(true)
                    .build();
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));

            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(mockCategory));

            // when
            BoardUpdateRequestDto updateRequestDto = dtoFactory.createUpdateRequestDto();
            updateRequestDto.setMethod(Method.SHARE);
            MultipartFile[] newPictures = dtoFactory.createFiles(2);
            boardService.update(boardId, memberId, updateRequestDto, newPictures);

            // then
            verify(boardRepository).deleteAllByMemberAndTmpIsTrueAndIdIsNot(mockMember, boardId);
            verify(boardPictureService).deletePicturesIfExist(updateRequestDto.getRemovePictures());
            verify(boardPictureService).uploadPicturesIfExistAndUnderLimit(newPictures, mockBoard);
            assertThat(mockBoard.getPrice()).isEqualTo(0);
            assertThat(mockBoard.getCategory().getId()).isEqualTo(2L);
            assertThat(mockBoard.getSuggest()).isEqualTo(false);
            assertThat(mockBoard.getDescription()).isEqualTo("It's my MacBook description");
            assertThat(mockBoard.getPlace()).isEqualTo("Amsa");
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
            Board mockBoard = dtoFactory.createMockBoard(boardId, mockMember, mockCategory, null);
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(mockCategory));

            // when
            BoardUpdateRequestDto updateRequestDto = dtoFactory.createUpdateRequestDto();
            boardService.update(boardId, memberId, updateRequestDto, dtoFactory.createFiles(2));

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
            assertThatThrownBy(() -> boardService.update(1L, 1L, dtoFactory.createUpdateRequestDto(), dtoFactory.createFiles(2)))
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
            assertThatThrownBy(() -> boardService.update(1L, memberId, dtoFactory.createUpdateRequestDto(), dtoFactory.createFiles(2)))
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
            Board mockBoard = dtoFactory.createMockBoard(boardId, Member.builder().id(2L).build(), mock(Category.class), null);
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));

            // when
            // then
            assertThatThrownBy(() -> boardService.update(boardId, memberId, dtoFactory.createUpdateRequestDto(), dtoFactory.createFiles(2)))
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
            assertThatThrownBy(() -> boardService.update(boardId, memberId, dtoFactory.createUpdateRequestDto(), dtoFactory.createFiles(2)))
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

            Category mockCategory = Category.builder().id(1L).build();
            Long boardId = 1L;
            Board mockBoard = dtoFactory.createMockBoard(boardId, mockMember, mockCategory, null);
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(mockCategory));

            BoardUpdateRequestDto updateRequestDto = dtoFactory.createUpdateRequestDto();
            MultipartFile[] newPictures = dtoFactory.createFiles(20);
            doThrow(FileUploadLimitException.class).when(boardPictureService).uploadPicturesIfExistAndUnderLimit(newPictures, mockBoard);

            // when
            // then
            assertThatThrownBy(() -> boardService.update(boardId, memberId, updateRequestDto, newPictures))
                    .isInstanceOf(FileUploadLimitException.class)
                    .hasMessage(FILE_UPLOAD_LIMIT.getMessage());
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
    class GetTmpBoardDetail {

        @Test
        @DisplayName(SUCCESS)
        void tmpBoardDetailSuccess() {
            // given
            Long memberId = 1L;
            Member mockMember = Member.builder().id(memberId).build();
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));

            Long boardId = 1L;
            List<BoardPicture> mockPictures = dtoFactory.createBoardPictures(2);
            Board mockBoard = dtoFactory.createMockBoard(boardId, mockMember, mock(Category.class), mockPictures);
            when(boardRepository.findFirstByMemberAndTmpIsTrueOrderByCreateDateDesc(any())).thenReturn(Optional.of(mockBoard));

            // when
            BoardDetailResponseDto boardDetail = boardService.getTmpBoardDetail(memberId);

            // then
            assertThat(boardDetail.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName(FAIL_TMP_BOARDS_NOT_FOUND)
        void tmpBoardDetailFailedNoTmpBoards() {
            // given
            Long memberId = 1L;
            Member mockMember = Member.builder().id(memberId).build();
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));
            when(boardRepository.findFirstByMemberAndTmpIsTrueOrderByCreateDateDesc(any())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.getTmpBoardDetail(memberId))
                    .isInstanceOf(TmpBoardNotFoundException.class)
                    .hasMessage(TMP_BOARD_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void tmpBoardDetailFailMemberNotFound() {
            // given
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.getTmpBoardDetail(1L))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }
    }
}