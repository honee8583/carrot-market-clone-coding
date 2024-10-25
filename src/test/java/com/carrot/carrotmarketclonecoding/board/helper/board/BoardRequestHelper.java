package com.carrot.carrotmarketclonecoding.board.helper.board;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;

import com.carrot.carrotmarketclonecoding.util.ControllerRequestUtil;
import lombok.AllArgsConstructor;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@TestComponent
@AllArgsConstructor
public class BoardRequestHelper extends ControllerRequestUtil {

    private final MockMvc mvc;

    private static final String REGISTER_BOARD_URL = "/board/register";
    private static final String REGISTER_TMP_BOARD_URL = "/board/register/tmp";
    private static final String GET_BOARD_DETAIL_URL = "/board/{id}";
    private static final String SEARCH_BOARDS_URL = "/board";
    private static final String SEARCH_MY_BOARDS_URL = "/board/my";
    private static final String UPDATE_BOARD_URL = "/board/{id}";
    private static final String DELETE_BOARD_URL = "/board/{id}";
    private static final String GET_TMP_BOARD_URL = "/board/tmp";

    public ResultActions requestRegisterBoard(MockMultipartFile request,
                                              MockMultipartFile[] pictures) throws Exception {
        return request(REGISTER_BOARD_URL, request, pictures);
    }

    public ResultActions requestRegisterTmpBoard(MockMultipartFile request,
                                                 MockMultipartFile[] pictures) throws Exception {
        return request(REGISTER_TMP_BOARD_URL, request, pictures);
    }

    private ResultActions request(String url,
                                  MockMultipartFile request,
                                  MockMultipartFile[] pictures) throws Exception {
        return mvc.perform(
                requestWithCsrfAndSetContentType(
                        requestWithMultipartFiles(multipart(url), pictures).file(request),
                        MediaType.MULTIPART_FORM_DATA
                ));
    }

    public ResultActions requestGetBoardDetail() throws Exception {
        return mvc.perform(get(GET_BOARD_DETAIL_URL, 1L).accept(MediaType.APPLICATION_JSON));
    }

    public ResultActions requestGetSearchBoards() throws Exception {
        return mvc.perform(get(SEARCH_BOARDS_URL)
                .param("categoryId", "1")
                .param("keyword", "title")
                .param("minPrice", "0")
                .param("maxPrice", "20000")
                .param("order", "NEWEST")
                .param("page", "0")
        );
    }

    public ResultActions requestSearchMyBoards() throws Exception {
        return mvc.perform(get(SEARCH_MY_BOARDS_URL)
                .param("status", "SELL")
                .param("hide", "true"));
    }

    public ResultActions requestUpdateBoard(MockMultipartFile[] newPictures, MockMultipartFile updateRequest) throws Exception {
        return mvc.perform(
                requestWithCsrfAndSetContentType(
                        requestWithMultipartFiles(multipart(UPDATE_BOARD_URL, 1L), newPictures)
                                .file(updateRequest)
                                .with(request -> {
                                    request.setMethod("PATCH");
                                    return request;
                                }),
                        MediaType.MULTIPART_FORM_DATA
                ));
    }

    public ResultActions requestDeleteBoard() throws Exception {
        return mvc.perform(requestWithCsrf(delete(DELETE_BOARD_URL, 1L)));
    }

    public ResultActions requestGetTmpBoard() throws Exception {
        return mvc.perform(get(GET_TMP_BOARD_URL));
    }
}
