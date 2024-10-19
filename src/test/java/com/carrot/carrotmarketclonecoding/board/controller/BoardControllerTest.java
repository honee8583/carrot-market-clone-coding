package com.carrot.carrotmarketclonecoding.board.controller;

import static com.carrot.carrotmarketclonecoding.board.displayname.BoardTestDisplayNames.MESSAGE.*;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.*;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.*;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrot.carrotmarketclonecoding.auth.config.WithCustomMockUser;
import com.carrot.carrotmarketclonecoding.board.domain.enums.Method;
import com.carrot.carrotmarketclonecoding.board.domain.enums.Status;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardRegisterRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardRequestDto.BoardUpdateRequestDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardDetailResponseDto;
import com.carrot.carrotmarketclonecoding.board.dto.BoardResponseDto.BoardSearchResponseDto;
import com.carrot.carrotmarketclonecoding.board.dto.validation.BoardRegisterValidationMessage.MESSAGE;
import com.carrot.carrotmarketclonecoding.board.service.impl.BoardServiceImpl;
import com.carrot.carrotmarketclonecoding.common.exception.BoardNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.CategoryNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.FileUploadLimitException;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.UnauthorizedAccessException;
import com.carrot.carrotmarketclonecoding.common.response.PageResponseDto;
import com.carrot.carrotmarketclonecoding.util.RestDocsTestUtil;
import com.carrot.carrotmarketclonecoding.util.ResultFields;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.RequestPartFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;
import org.springframework.restdocs.request.QueryParametersSnippet;
import org.springframework.restdocs.request.RequestPartsSnippet;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@WithCustomMockUser
@WebMvcTest(controllers = BoardController.class)
class BoardControllerTest extends RestDocsTestUtil {

    @MockBean
    private BoardServiceImpl boardService;

    @Nested
    @DisplayName(BOARD_REGISTER_CONTROLLER_TEST)
    class BoardRegister {

        private MockMultipartFile[] pictures;
        private MockMultipartFile registerRequest;

        private static final String REGISTER_BOARD_URL = "/board/register";
        private static final String REGISTER_TMP_BOARD_URL = "/board/register/tmp";

        @BeforeEach
        public void setUp() throws Exception {
            this.pictures = createMockMultipartFiles("pictures", 2);
            this.registerRequest = createRegisterRequestMultipartFile(BoardRegisterRequestDto.builder()
                    .title("title")
                    .categoryId(1L)
                    .method(Method.SELL)
                    .price(200000)
                    .suggest(true)
                    .description("description")
                    .place("amsa")
                    .build());
        }

        @Test
        @DisplayName(SUCCESS)
        void boardRegisterSuccess() throws Exception {
            // given
            // when
            when(boardService.register(any(), any(), any(), anyBoolean())).thenReturn(1L);

            // then
            assertRegisterBoardSuccess(ResultFields.builder()
                    .resultMatcher(status().isCreated())
                    .status(201)
                    .result(true)
                    .message(BOARD_REGISTER_SUCCESS.getMessage())
                    .build());
        }

        @Test
        @DisplayName(SUCCESS_REGISTER_TMP_BOARD)
        void boardRegisterTmpSuccess() throws Exception {
            // given
            // when
            when(boardService.register(anyLong(), any(), any(), anyBoolean())).thenReturn(1L);

            // then
            assertRegisterTmpBoardSuccess(ResultFields.builder()
                    .resultMatcher(status().isCreated())
                    .status(201)
                    .result(true)
                    .message(BOARD_REGISTER_TEMPORARY_SUCCESS.getMessage())
                    .build());
        }

        @Test
        @DisplayName(FAIL_FILE_COUNT_OVER_10)
        void boardRegisterFileUploadLimitExceeded() throws Exception {
            // given
            // when
            when(boardService.register(anyLong(), any(), any(), anyBoolean())).thenThrow(FileUploadLimitException.class);

            // then
            assertRegisterBoardFailed(ResultFields.builder()
                    .resultMatcher(status().isInternalServerError())
                    .status(500)
                    .result(false)
                    .message(FILE_UPLOAD_LIMIT.getMessage())
                    .build());
        }

        @Test
        @DisplayName(FAIL_INPUT_NOT_VALID)
        void boardRegisterValidationFailed() throws Exception {
            // given
            registerRequest = createRegisterRequestMultipartFile(new BoardRegisterRequestDto());

            // when
            // then
            assertRegisterBoardFailedInvalidInput(ResultFields.builder()
                    .resultMatcher(status().isBadRequest())
                    .status(400)
                    .result(false)
                    .message(INPUT_NOT_VALID.getMessage())
                    .build());
        }

        private Map<String, String> createInputInvalidResponseData() {
            Map<String, String> responseData = new HashMap<>();
            responseData.put("title", MESSAGE.TITLE_NOT_VALID);
            responseData.put("categoryId", MESSAGE.CATEGORY_NOT_VALID);
            responseData.put("method", "잘못된 입력입니다!");
            responseData.put("price", MESSAGE.PRICE_NOT_VALID);
            responseData.put("suggest", MESSAGE.SUGGEST_NOT_VALID);
            responseData.put("description", MESSAGE.DESCRIPTION_NOT_VALID);
            responseData.put("place", MESSAGE.PLACE_NOT_VALID);
            return responseData;
        }

        @Test
        @DisplayName(FAIL_WRITER_NOT_FOUND)
        void boardRegisterMemberNotFound() throws Exception {
            // given
            // when
            when(boardService.register(anyLong(), any(), any(), anyBoolean())).thenThrow(MemberNotFoundException.class);

            // then
            assertRegisterBoardFailed(ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build());
        }

        @Test
        @DisplayName(FAIL_CATEGORY_NOT_FOUND)
        void boardRegisterCategoryNotFound() throws Exception {
            // given
            // when
            when(boardService.register(anyLong(), any(), any(), anyBoolean())).thenThrow(CategoryNotFoundException.class);

            // then
            assertRegisterBoardFailed(ResultFields.builder()
                    .resultMatcher(status().isBadRequest())
                    .status(400)
                    .result(false)
                    .message(CATEGORY_NOT_FOUND.getMessage())
                    .build());
        }

        /**
         * TODO REFACTOR 클래스 분리 A
         * 게시글 작성 성공 테스트
         */
        private void assertRegisterBoardSuccess(ResultFields resultFields) throws Exception {
            assertResponseResult(requestRegisterBoard(REGISTER_BOARD_URL), resultFields)
                    .andExpect(jsonPath("$.data", equalTo(null)))
                    .andDo(createDocument());
        }

        /**
         * TODO REFACTOR 클래스 분리 A
         * 임시 게시글 작성 성공 테스트
         */
        private void assertRegisterTmpBoardSuccess(ResultFields resultFields) throws Exception {
            assertResponseResult(
                    requestRegisterBoard(REGISTER_TMP_BOARD_URL), resultFields)
                    .andExpect(jsonPath("$.data", equalTo(null)))
                    .andDo(createDocument());
        }

        private RestDocumentationResultHandler createDocument() {
            return restDocs.document(
                    documentRequestParts(),
                    requestPartBody("registerRequest"),
                    documentRequestPartFields(),
                    responseFields(createResponseResultDescriptor())
            );
        }

        private RequestPartsSnippet documentRequestParts() {
            return requestParts(
                    partWithName("registerRequest").description("게시글 작성 입력값"),
                    partWithName("pictures").description("첨부사진")
            );
        }

        private RequestPartFieldsSnippet documentRequestPartFields() {
            return requestPartFields("registerRequest",
                    fieldWithPath("title").description("게시글 제목"),
                    fieldWithPath("categoryId").description("카테고리 아이디"),
                    fieldWithPath("method").description("거래 방식 (SELL/SHARE)"),
                    fieldWithPath("price").description("상품 가격"),
                    fieldWithPath("suggest").description("가격 제안 여부"),
                    fieldWithPath("description").description("상품 설명"),
                    fieldWithPath("place").description("거래 희망 장소")
            );
        }

        /**
         * TODO REFACTOR class A
         * 게시글 작성 실패 테스트
         */
        private void assertRegisterBoardFailed(ResultFields resultFields) throws Exception {
            assertResponseResult(requestRegisterBoard(REGISTER_BOARD_URL), resultFields)
                    .andExpect(jsonPath("$.data", equalTo(null)))
                    .andDo(restDocs.document(
                            responseFields(createResponseResultDescriptor())
                    ));
        }

        /**
         * TODO REFACTOR class A
         * 게시글 작성 입력값 유효x
         */
        private void assertRegisterBoardFailedInvalidInput(ResultFields resultFields) throws Exception {
            assertResponseResult(requestRegisterBoard(REGISTER_BOARD_URL), resultFields)
                    .andExpect(jsonPath("$.data", equalTo(createInputInvalidResponseData())))
                    .andDo(restDocs.document(
                            responseFields(
                                    fieldWithPath("status").description("응답 상태"),
                                    fieldWithPath("result").description("응답 결과"),
                                    fieldWithPath("message").description("응답 메시지"),
                                    fieldWithPath("data.title").description("응답 본문 - 제목을 입력하지 않음"),
                                    fieldWithPath("data.categoryId").description("응답 본문 - 카테고리아이디를 입력하지 않음"),
                                    fieldWithPath("data.method").description("응답 본문 - 거래방식을 입력하지 않음"),
                                    fieldWithPath("data.price").description("응답 본문 - 가격을 입력하지 않음"),
                                    fieldWithPath("data.suggest").description("응답 본문 - 가격 제안 여부를 입력하지 않음"),
                                    fieldWithPath("data.description").description("응답 본문 - 상품 설명을 입력하지 않음"),
                                    fieldWithPath("data.place").description("응답 본문 - 거래 장소를 입력하지 않음")
                            )
                    ));
        }

        /**
         * TODO REFACTOR class A
         * 게시글 작성 API 요청
         */
        private ResultActions requestRegisterBoard(String url) throws Exception {
            return mvc.perform(
                    requestWithCsrfAndSetContentType(
                            requestMultipartFiles(multipart(url), pictures).file(registerRequest),
                            MediaType.MULTIPART_FORM_DATA
                    )
            );
        }

        /**
         * // TODO REFACTOR class A
         * 게시글 작성 RequestPart 생성
         */
        private MockMultipartFile createRegisterRequestMultipartFile(BoardRegisterRequestDto registerRequestDto) throws Exception {
            String registerRequestJson = new ObjectMapper().writeValueAsString(registerRequestDto);
            return new MockMultipartFile(
                    "registerRequest",
                    "registerRequest.json",
                    "application/json",
                    registerRequestJson.getBytes()
            );
        }
    }

    @Nested
    @DisplayName(BOARD_DETAIL_CONTROLLER_TEST)
    class GetBoardDetail {

        private static final String URL = "/board/{id}";

        @Test
        @DisplayName(SUCCESS)
        void boardDetailSuccess() throws Exception {
            // given
            Long boardId = 1L;
            BoardDetailResponseDto response = createBoardDetailResponseDto();

            // when
            when(boardService.detail(anyLong(), any())).thenReturn(response);

            // then
            assertGetBoardDetailSuccess(ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(BOARD_GET_DETAIL_SUCCESS.getMessage())
                    .build(), "$.data.id", boardId.intValue());
        }

        @Test
        @DisplayName(FAIL_BOARD_NOT_FOUND)
        void boardDetailBoardNotFound() throws Exception {
            // given
            // when
            when(boardService.detail(anyLong(), any())).thenThrow(new BoardNotFoundException());

            // then
            assertGetBoardDetailFailed(ResultFields.builder()
                    .resultMatcher(status().isBadRequest())
                    .status(400)
                    .result(false)
                    .message(BOARD_NOT_FOUND.getMessage())
                    .build());
        }

        private void assertGetBoardDetailSuccess(ResultFields resultFields, String dataJsonPath, Object data) throws Exception {
            assertResponseResult(requestGetBoardDetail(), resultFields)
                    .andExpect(jsonPath(dataJsonPath, equalTo(data)))
                    .andDo(restDocs.document(
                            documentPathParameters(),
                            responseFields(createBoardDetailSuccessDescriptor())
                    ));
        }

        private void assertGetBoardDetailFailed(ResultFields resultFields) throws Exception {
            assertResponseResult(requestGetBoardDetail(), resultFields)
                    .andExpect(jsonPath("$.data", equalTo(null)))
                    .andDo(restDocs.document(
                            documentPathParameters(),
                            responseFields(createResponseResultDescriptor())
                    ));
        }

        private ResultActions requestGetBoardDetail() throws Exception {
            return mvc.perform(get(URL, 1L).accept(MediaType.APPLICATION_JSON));
        }

        private PathParametersSnippet documentPathParameters() {
            return pathParameters(parameterWithName("id").description("게시글 ID"));
        }

        private FieldDescriptor[] createBoardDetailSuccessDescriptor() {
            return new FieldDescriptor[] {
                    fieldWithPath("status").description("응답 상태"),
                    fieldWithPath("result").description("응답 결과"),
                    fieldWithPath("message").description("응답 메시지"),
                    fieldWithPath("data.id").description("게시글 아이디"),
                    fieldWithPath("data.writer").description("작성자"),
                    fieldWithPath("data.place").description("거래 장소"),
                    fieldWithPath("data.profileUrl").description("프로필 사진 URL"),
                    fieldWithPath("data.status").description("거래 상태"),
                    fieldWithPath("data.title").description("게시글 제목"),
                    fieldWithPath("data.category").description("카테고리"),
                    fieldWithPath("data.method").description("거래 방식"),
                    fieldWithPath("data.price").description("상품 가격"),
                    fieldWithPath("data.suggest").description("가격 제안 여부"),
                    fieldWithPath("data.createDate").description("게시글 생성일"),
                    fieldWithPath("data.description").description("상품 설명"),
                    fieldWithPath("data.pictures").description("상품 사진"),
                    fieldWithPath("data.chat").description("채팅 수"),
                    fieldWithPath("data.like").description("좋아요수"),
                    fieldWithPath("data.visit").description("조회수")
            };
        }
    }

    @Nested
    @DisplayName(BOARD_SEARCH_CONTROLLER_TEST)
    class SearchBoards {

        private static final String URL = "/board";

        @Test
        @DisplayName(SUCCESS)
        void searchBoardsSuccess() throws Exception {
            // given
            List<BoardSearchResponseDto> searchResponseDtos = createBoardSearchResponseDtos(2);

            // when
            PageResponseDto<BoardSearchResponseDto> response = createBoardSearchResponse(0, 10, 2, searchResponseDtos);
            when(boardService.search(anyLong(), any(), any())).thenReturn(response);

            // then
            assertSearchBoardsSuccess(ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(SEARCH_BOARDS_SUCCESS.getMessage())
                    .build(), 2);
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void searchBoardsMemberNotFound() throws Exception {
            // given
            // when
            doThrow(MemberNotFoundException.class).when(boardService).search(any(), any(), any());

            // then
            assertSearchBoardsFailed(ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build());
        }

        private void assertSearchBoardsSuccess(ResultFields resultFields, int contentSize) throws Exception {
            assertResponseResult(requestGetSearchBoards(), resultFields)
                    .andExpect(jsonPath("$.data.contents.size()", equalTo(contentSize)))
                    .andDo(restDocs.document(
                            documentQueryParameters(),
                            documentResponseFields()
                    ));
        }

        private void assertSearchBoardsFailed(ResultFields resultFields) throws Exception {
            assertResponseResult(requestGetSearchBoards(), resultFields)
                    .andExpect(jsonPath("$.data", equalTo(null)))
                    .andDo(restDocs.document(
                            documentQueryParameters(),
                            responseFields(createResponseResultDescriptor())
                    ));
        }

        private ResultActions requestGetSearchBoards() throws Exception {
            return mvc.perform(get(URL)
                    .param("categoryId", "1")
                    .param("keyword", "title")
                    .param("minPrice", "0")
                    .param("maxPrice", "20000")
                    .param("order", "NEWEST")
                    .param("page", "0")
            );
        }

        private QueryParametersSnippet documentQueryParameters() {
            return queryParameters(
                    parameterWithName("categoryId").description("카테고리 아이디"),
                    parameterWithName("keyword").description("검색 키워드"),
                    parameterWithName("minPrice").description("최소 가격"),
                    parameterWithName("maxPrice").description("최대 가격"),
                    parameterWithName("order").description("정렬 순서"),
                    parameterWithName("page").description("요청 페이지 번호")
            );
        }

        private ResponseFieldsSnippet documentResponseFields() {
            return responseFields(
                    fieldWithPath("status").description("응답 상태"),
                    fieldWithPath("result").description("응답 결과"),
                    fieldWithPath("message").description("응답 메시지"),
                    fieldWithPath("data.contents[].id").description("게시글 아이디"),
                    fieldWithPath("data.contents[].pictureUrl").description("사진 URL"),
                    fieldWithPath("data.contents[].title").description("게시글 제목"),
                    fieldWithPath("data.contents[].place").description("거래 장소"),
                    fieldWithPath("data.contents[].createDate").description("게시글 생성일"),
                    fieldWithPath("data.contents[].price").description("상품 가격"),
                    fieldWithPath("data.contents[].like").description("좋아요 수"),
                    fieldWithPath("data.totalPage").description("전체 페이지 개수"),
                    fieldWithPath("data.totalElements").description("전체 데이터 수"),
                    fieldWithPath("data.first").description("첫페이지 여부"),
                    fieldWithPath("data.last").description("마지막페이지 여부"),
                    fieldWithPath("data.numberOfElements").description("현재 페이지 데이터 개수")
            );
        }
    }

    @Nested
    @DisplayName(BOARD_MY_DETAIL_CONTROLLER_TEST)
    class SearchBoardByStatus {

        private static final String URL = "/board/my";

        @Test
        @DisplayName(SUCCESS)
        void searchBoardsByStatusSuccess() throws Exception {
            // given
            PageResponseDto<BoardSearchResponseDto> response = createBoardSearchResponse(0, 10, 30, createBoardSearchResponseDtos(30));

            // when
            when(boardService.searchMyBoards(anyLong(), any(), any())).thenReturn(response);

            // then
            assertSearchMyBoardsSuccess(ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(SEARCH_BOARDS_SUCCESS.getMessage())
                    .build());
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void searchBoardsByStatusFailMemberNotFound() throws Exception {
            // given
            // when
            doThrow(MemberNotFoundException.class).when(boardService).searchMyBoards(anyLong(), any(), any());

            // then
            assertSearchMyBoardsFailed(ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build());
        }

        private void assertSearchMyBoardsSuccess(ResultFields resultFields) throws Exception {
            assertResponseResult(requestSearchBoardsByStatus(), resultFields)
                    .andExpect(jsonPath("$.data.contents.size()", equalTo(30)))
                    .andExpect(jsonPath("$.data.totalPage", equalTo(3)))
                    .andExpect(jsonPath("$.data.totalElements", equalTo(30)))
                    .andExpect(jsonPath("$.data.first", equalTo(true)))
                    .andExpect(jsonPath("$.data.last", equalTo(false)))
                    .andExpect(jsonPath("$.data.numberOfElements", equalTo(30)))
                    .andDo(createDocument());
        }

        private RestDocumentationResultHandler createDocument() {
            return restDocs.document(documentQueryParameters(), documentResponseFields());
        }

        private QueryParametersSnippet documentQueryParameters() {
            return queryParameters(
                    parameterWithName("status").description("거래 상태"),
                    parameterWithName("hide").description("숨김 여부")
            );
        }

        private ResponseFieldsSnippet documentResponseFields() {
            return responseFields(
                    fieldWithPath("status").description("응답 상태"),
                    fieldWithPath("result").description("응답 결과"),
                    fieldWithPath("message").description("응답 메시지"),
                    fieldWithPath("data.contents[].id").description("게시글 아이디"),
                    fieldWithPath("data.contents[].pictureUrl").description("사진 URL"),
                    fieldWithPath("data.contents[].title").description("게시글 제목"),
                    fieldWithPath("data.contents[].place").description("거래 장소"),
                    fieldWithPath("data.contents[].createDate").description("게시글 생성일"),
                    fieldWithPath("data.contents[].price").description("상품 가격"),
                    fieldWithPath("data.contents[].like").description("좋아요 수"),
                    fieldWithPath("data.totalPage").description("전체 페이지 개수"),
                    fieldWithPath("data.totalElements").description("전체 데이터 수"),
                    fieldWithPath("data.first").description("첫페이지 여부"),
                    fieldWithPath("data.last").description("마지막페이지 여부"),
                    fieldWithPath("data.numberOfElements").description("현재 페이지 데이터 개수")
            );
        }

        private void assertSearchMyBoardsFailed(ResultFields resultFields) throws Exception {
            assertResponseResult(requestSearchBoardsByStatus(), resultFields)
                    .andExpect(jsonPath("$.data", equalTo(null)))
                    .andDo(restDocs.document(
                            responseFields(createResponseResultDescriptor())
                    ));
        }

        private ResultActions requestSearchBoardsByStatus() throws Exception {
            return mvc.perform(get(URL)
                    .param("status", "SELL")
                    .param("hide", "true"));
        }
    }

    @Nested
    @DisplayName(BOARD_UPDATE_CONTROLLER_TEST)
    class UpdateBoard {

        MockMultipartFile updateRequest;
        MockMultipartFile[] newPictures;

        private static final String URL = "/board/{id}";

        @BeforeEach
        public void setUp() throws Exception {
            this.updateRequest = createUpdateRequest();
            this.newPictures = createMockMultipartFiles("newPictures", 3);
        }

        @Test
        @DisplayName(SUCCESS)
        void boardUpdateSuccess() throws Exception {
            // given
            // when
            doNothing().when(boardService).update(anyLong(), anyLong(), any(BoardUpdateRequestDto.class), any());

            // then
            assertUpdateBoard(ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(BOARD_UPDATE_SUCCESS.getMessage())
                    .build());
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void boardUpdateFailMemberNotFound() throws Exception {
            // given
            // when
            doThrow(MemberNotFoundException.class).when(boardService).update(anyLong(), anyLong(), any(BoardUpdateRequestDto.class), any());

            // then
            assertUpdateBoard(ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build());
        }

        @Test
        @DisplayName(FAIL_BOARD_NOT_FOUND)
        void boardUpdateFailBoardNotFound() throws Exception {
            // given
            // when
            doThrow(BoardNotFoundException.class).when(boardService).update(anyLong(), anyLong(), any(BoardUpdateRequestDto.class), any());

            // then
            assertUpdateBoard(ResultFields.builder()
                    .resultMatcher(status().isBadRequest())
                    .status(400)
                    .result(false)
                    .message(BOARD_NOT_FOUND.getMessage())
                    .build());
        }

        @Test
        @DisplayName(FAIL_MEMBER_IS_NOT_WRITER)
        void boardUpdateFailMemberIsNotWriter() throws Exception {
            // given
            // when
            doThrow(UnauthorizedAccessException.class).when(boardService).update(anyLong(), anyLong(), any(BoardUpdateRequestDto.class), any());

            // then
            assertUpdateBoard(ResultFields.builder()
                    .resultMatcher(status().isForbidden())
                    .status(403)
                    .result(false)
                    .message(UNAUTHORIZED_ACCESS.getMessage())
                    .build());
        }

        // TODO BoardResponseValidator
        private void assertUpdateBoard(ResultFields resultFields) throws Exception {
            assertResponseResult(requestUpdateBoard(), resultFields)
                    .andExpect(jsonPath("$.data", equalTo(null)))
                    .andDo(createDocument());
        }

        private ResultActions requestUpdateBoard() throws Exception {
            return mvc.perform(
                    requestWithCsrfAndSetContentType(
                            requestMultipartFiles(multipart(URL, 1L), newPictures)
                                    .file(updateRequest)
                                    .with(request -> {
                                        request.setMethod("PATCH");
                                        return request;
                                    }),
                            MediaType.MULTIPART_FORM_DATA
                    )
            );
        }

        // TODO BoardDocumentCreator
        private RestDocumentationResultHandler createDocument() {
            return restDocs.document(
                    pathParameters(
                            parameterWithName("id").description("수정할 게시글 ID")
                    ),
                    requestParts(
                            partWithName("updateRequest").description("게시글 수정 입력값"),
                            partWithName("newPictures").description("새 첨부사진")
                    ),
                    requestPartBody("updateRequest"),
                    requestPartFields("updateRequest",
                            fieldWithPath("title").description("게시글 제목"),
                            fieldWithPath("categoryId").description("카테고리 아이디"),
                            fieldWithPath("method").description("거래 방식 (SELL/SHARE)"),
                            fieldWithPath("price").description("상품 가격"),
                            fieldWithPath("suggest").description("가격 제안 여부"),
                            fieldWithPath("description").description("상품 설명"),
                            fieldWithPath("place").description("거래 희망 장소"),
                            fieldWithPath("removePictures").description("삭제할 이미지 아이디")
                    ),
                    responseFields(createResponseResultDescriptor())
            );
        }

        // TODO BoardTestDataFactory
        private MockMultipartFile createUpdateRequest() throws Exception {
            String updateRequestJson = new ObjectMapper().writeValueAsString(createUpdateRequestDto());
            return new MockMultipartFile(
                    "updateRequest",
                    "updateRequest.json",
                    "application/json",
                    updateRequestJson.getBytes()
            );
        }

        private BoardUpdateRequestDto createUpdateRequestDto() {
            return BoardUpdateRequestDto.builder()
                    .title("Sell My MacBook")
                    .categoryId(2L)
                    .method(Method.SELL)
                    .price(1000000)
                    .suggest(false)
                    .description("It's my MacBook description")
                    .place("Amsa")
                    .removePictures(new Long[]{1L, 2L, 3L})
                    .build();
        }
    }

    @Nested
    @DisplayName(BOARD_DELETE_CONTROLLER_TEST)
    class DeleteBoard {

        private static final String URL = "/board/{id}";

        @Test
        @DisplayName(SUCCESS)
        void deleteBoardSuccess() throws Exception {
            // given
            // when
            doNothing().when(boardService).delete(anyLong(), anyLong());

            // then
            assertDeleteBoard(ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(BOARD_DELETE_SUCCESS.getMessage())
                    .build());
        }

        @Test
        @DisplayName(FAIL_BOARD_NOT_FOUND)
        void deleteBoardFailBoardNotFound() throws Exception {
            // given
            // when
            doThrow(BoardNotFoundException.class).when(boardService).delete(anyLong(), anyLong());

            // then
            assertDeleteBoard(ResultFields.builder()
                    .resultMatcher(status().isBadRequest())
                    .status(400)
                    .result(false)
                    .message(BOARD_NOT_FOUND.getMessage())
                    .build());
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void deleteBoardFailMemberNotFound() throws Exception {
            // given
            // when
            doThrow(MemberNotFoundException.class).when(boardService).delete(anyLong(), anyLong());

            // then
            assertDeleteBoard(ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build());
        }

        @Test
        @DisplayName(FAIL_MEMBER_IS_NOT_WRITER)
        void deleteBoardFailMemberIsNotWriter() throws Exception {
            // given
            // when
            doThrow(UnauthorizedAccessException.class).when(boardService).delete(anyLong(), anyLong());

            // then
            assertDeleteBoard(ResultFields.builder()
                    .resultMatcher(status().isForbidden())
                    .status(403)
                    .result(false)
                    .message(UNAUTHORIZED_ACCESS.getMessage())
                    .build());
        }

        private void assertDeleteBoard(ResultFields resultFields) throws Exception {
            assertResponseResult(requestDeleteBoard(), resultFields)
                    .andExpect(jsonPath("$.data", equalTo(null)))
                    .andDo(createDocument());
        }

        private ResultActions requestDeleteBoard() throws Exception {
            return mvc.perform(requestWithCsrf(delete(URL, 1L)));
        }

        private RestDocumentationResultHandler createDocument() {
            return restDocs.document(
                    pathParameters(
                            parameterWithName("id").description("삭제할 게시글 ID")
                    ),
                    responseFields(createResponseResultDescriptor())
            );
        }
    }

    @Nested
    @DisplayName(BOARD_GET_TMP_CONTROLLER_TEST)
    class TmpBoardDetail {

        private static final String URL = "/board/tmp";

        @Test
        @DisplayName(SUCCESS)
        void boardDetailSuccess() throws Exception {
            // given
            BoardDetailResponseDto response = createBoardDetailResponseDto();

            // when
            when(boardService.tmpBoardDetail(anyLong())).thenReturn(response);

            // then
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(BOARD_GET_TMP_SUCCESS.getMessage())
                    .build();
            assertGetTmpBoardDetailSuccess(resultFields, "$.data.id", response.getId().intValue());
        }

        @Test
        @DisplayName(SUCCESS_NO_TMP_BOARDS)
        void boardDetailSuccessNotTmpBoard() throws Exception {
            // given
            // when
            when(boardService.tmpBoardDetail(anyLong())).thenReturn(null);

            // then
            assertTmpBoardDetailFailed(ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(BOARD_GET_TMP_SUCCESS.getMessage())
                    .build());
        }

        @Test
        @DisplayName(FAIL_MEMBER_NOT_FOUND)
        void boardDetailFailMemberNotFound() throws Exception {
            // given
            // when
            doThrow(MemberNotFoundException.class).when(boardService).tmpBoardDetail(anyLong());

            // then
            assertTmpBoardDetailFailed(ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build());
        }

        private void assertGetTmpBoardDetailSuccess(ResultFields resultFields, String jsonPath, Object data) throws Exception {
            assertResponseResult(mvc.perform(get(URL)), resultFields)
                    .andExpect(jsonPath(jsonPath, equalTo(data)))
                    .andDo(restDocs.document(responseFields(createBoardDetailSuccessDescriptor())));
        }

        private void assertTmpBoardDetailFailed(ResultFields resultFields) throws Exception {
            assertResponseResult(mvc.perform(get(URL)), resultFields)
                    .andExpect(jsonPath("$.data", equalTo(null)))
                    .andDo(restDocs.document(responseFields(createResponseResultDescriptor())));
        }

        private FieldDescriptor[] createBoardDetailSuccessDescriptor() {
            return new FieldDescriptor[] {
                    fieldWithPath("status").description("응답 상태"),
                    fieldWithPath("result").description("응답 결과"),
                    fieldWithPath("message").description("응답 메시지"),
                    fieldWithPath("data.id").description("게시글 아이디"),
                    fieldWithPath("data.writer").description("작성자"),
                    fieldWithPath("data.place").description("거래 장소"),
                    fieldWithPath("data.profileUrl").description("프로필 사진 URL"),
                    fieldWithPath("data.status").description("거래 상태"),
                    fieldWithPath("data.title").description("게시글 제목"),
                    fieldWithPath("data.category").description("카테고리"),
                    fieldWithPath("data.method").description("거래 방식"),
                    fieldWithPath("data.price").description("상품 가격"),
                    fieldWithPath("data.suggest").description("가격 제안 여부"),
                    fieldWithPath("data.createDate").description("게시글 생성일"),
                    fieldWithPath("data.description").description("상품 설명"),
                    fieldWithPath("data.pictures").description("상품 사진"),
                    fieldWithPath("data.chat").description("채팅 수"),
                    fieldWithPath("data.like").description("좋아요수"),
                    fieldWithPath("data.visit").description("조회수")
            };
        }
    }

    private BoardDetailResponseDto createBoardDetailResponseDto() {
        return BoardDetailResponseDto.builder()
                .id(1L)
                .writer("User1")
                .place("Kangnam")
                .profileUrl("http://S3-ProfileUrl")
                .status(Status.SELL)
                .title("Sell My Keyboards")
                .category("가전기기")
                .method(Method.SELL)
                .price(100000)
                .suggest(false)
                .createDate(LocalDateTime.now())
                .description("This is my keyboard description")
                .pictures(new ArrayList<>())
                .chat(2)
                .like(10)
                .visit(100)
                .build();
    }

    private List<BoardSearchResponseDto> createBoardSearchResponseDtos(int size) {
        List<BoardSearchResponseDto> searchResponseDtos = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            searchResponseDtos.add(BoardSearchResponseDto.builder()
                    .id((long) i + 1)
                    .title("Sell My MacBook" + (i + 1))
                    .price(2000000)
                    .place("Amsa")
                    .createDate(LocalDateTime.now())
                    .pictureUrl("S3 Picture Url")
                    .build());
        }
        return searchResponseDtos;
    }

    private PageResponseDto<BoardSearchResponseDto> createBoardSearchResponse(int page, int size, int total, List<BoardSearchResponseDto> searchResponseDtos) {
        return new PageResponseDto<>(new PageImpl<>(searchResponseDtos, PageRequest.of(page, size), total));
    }

    private MockMultipartFile[] createMockMultipartFiles(String paramName, int size) {
        MockMultipartFile[] pictures = new MockMultipartFile[size];
        for (int i = 0; i < size; i++) {
            pictures[i] = new MockMultipartFile(
                    paramName,
                    "picture " + i + ".png",
                    "image/png",
                    ("picture " + i).getBytes());
        };
        return pictures;
    }
}