package com.carrot.carrotmarketclonecoding.board.helper.board;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;

import com.carrot.carrotmarketclonecoding.util.RestDocsHelper;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.RequestPartFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;
import org.springframework.restdocs.request.QueryParametersSnippet;
import org.springframework.restdocs.request.RequestPartsSnippet;
import org.springframework.test.web.servlet.ResultHandler;

@TestComponent
public class BoardRestDocsHelper extends RestDocsHelper {

    private final RestDocumentationResultHandler restDocs;

    public BoardRestDocsHelper(RestDocumentationResultHandler restDocs) {
        this.restDocs = restDocs;
    }

    public RestDocumentationResultHandler createRegisterBoardSuccessDocument() {
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

    public ResultHandler createCommonResponseDocument() {
        return createResponseResultDocument(restDocs);
    }

    public RestDocumentationResultHandler createRegisterBoardFailedInvalidInputDocument() {
        return restDocs.document(
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
        );
    }

    public RestDocumentationResultHandler createGetBoardDetailSuccessDocument() {
        return createGetBoardDetailDocument(documentGetBoardDetailSuccessResponseFields(createBoardDetailSuccessDescriptor()));
    }

    public RestDocumentationResultHandler createGetBoardDetailFailedDocument() {
        return createGetBoardDetailDocument(documentGetBoardDetailSuccessResponseFields(createResponseResultDescriptor()));
    }

    private RestDocumentationResultHandler createGetBoardDetailDocument(ResponseFieldsSnippet responseFieldsSnippet) {
        return restDocs.document(documentPathParameters(), responseFieldsSnippet);
    }

    private PathParametersSnippet documentPathParameters() {
        return pathParameters(parameterWithName("id").description("게시글 ID"));
    }

    private ResponseFieldsSnippet documentGetBoardDetailSuccessResponseFields(FieldDescriptor[] descriptors) {
        return responseFields(descriptors);
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

    public ResultHandler createSearchBoardsSuccessDocument() {
        return createSearchBoardsDocument(documentSearchBoardsSuccessResponseFields());
    }

    public ResultHandler createSearchBoardsFailedDocument() {
        return createSearchBoardsDocument(responseFields(createResponseResultDescriptor()));
    }

    private ResultHandler createSearchBoardsDocument(ResponseFieldsSnippet responseFieldsSnippet) {
        return restDocs.document(documentSearchBoardsQueryParameters(), responseFieldsSnippet);
    }

    private QueryParametersSnippet documentSearchBoardsQueryParameters() {
        return queryParameters(
                parameterWithName("categoryId").description("카테고리 아이디"),
                parameterWithName("keyword").description("검색 키워드"),
                parameterWithName("minPrice").description("최소 가격"),
                parameterWithName("maxPrice").description("최대 가격"),
                parameterWithName("order").description("정렬 순서"),
                parameterWithName("page").description("요청 페이지 번호")
        );
    }

    private ResponseFieldsSnippet documentSearchBoardsSuccessResponseFields() {
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

    public RestDocumentationResultHandler createSearchMyBoardsSuccessDocument() {
        return restDocs.document(documentSearchMyBoardsQueryParameters(), documentSearchMyBoardsResponseFields());
    }

    private QueryParametersSnippet documentSearchMyBoardsQueryParameters() {
        return queryParameters(
                parameterWithName("status").description("거래 상태"),
                parameterWithName("hide").description("숨김 여부")
        );
    }

    private ResponseFieldsSnippet documentSearchMyBoardsResponseFields() {
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

    public RestDocumentationResultHandler createUpdateBoardDocument() {
        return restDocs.document(
                documentUpdateBoardPathParameters(),
                documentUpdateBoardRequestParts(),
                requestPartBody("updateRequest"),
                documentUpdateBoardRequestPartFields(),
                responseFields(createResponseResultDescriptor())
        );
    }

    private RequestPartFieldsSnippet documentUpdateBoardRequestPartFields() {
        return requestPartFields("updateRequest",
                fieldWithPath("title").description("게시글 제목"),
                fieldWithPath("categoryId").description("카테고리 아이디"),
                fieldWithPath("method").description("거래 방식 (SELL/SHARE)"),
                fieldWithPath("price").description("상품 가격"),
                fieldWithPath("suggest").description("가격 제안 여부"),
                fieldWithPath("description").description("상품 설명"),
                fieldWithPath("place").description("거래 희망 장소"),
                fieldWithPath("removePictures").description("삭제할 이미지 아이디")
        );
    }

    private RequestPartsSnippet documentUpdateBoardRequestParts() {
        return requestParts(
                partWithName("updateRequest").description("게시글 수정 입력값"),
                partWithName("newPictures").description("새 첨부사진")
        );
    }

    private PathParametersSnippet documentUpdateBoardPathParameters() {
        return pathParameters(
                parameterWithName("id").description("수정할 게시글 ID")
        );
    }

    public ResultHandler createDeleteBoardSuccessDocument() {
        return restDocs.document(
                documentDeleteBoardPathParameters(),
                responseFields(createResponseResultDescriptor())
        );
    }

    private PathParametersSnippet documentDeleteBoardPathParameters() {
        return pathParameters(
                parameterWithName("id").description("삭제할 게시글 ID")
        );
    }

    public ResultHandler createGetTmpBoardSuccessDocument() {
        return restDocs.document(responseFields(createBoardDetailSuccessDescriptor()));
    }
}
