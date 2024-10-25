package com.carrot.carrotmarketclonecoding.board.helper.boardlike;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;

import com.carrot.carrotmarketclonecoding.util.ControllerRequestUtil;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@TestComponent
public class BoardLikeRequestHelper extends ControllerRequestUtil {

    private final MockMvc mvc;

    public BoardLikeRequestHelper(MockMvc mvc) {
        this.mvc = mvc;
    }

    private static final String ADD_BOARD_LIKE_URL = "/board/like/{id}";
    private static final String GET_USER_LIKED_BOARDS_URL = "/board/like";

    public ResultActions requestLikeBoard() throws Exception {
        return mvc.perform(requestWithCsrf(post(ADD_BOARD_LIKE_URL, 1L)));
    }

    public ResultActions requestGetUserLikedBoards() throws Exception {
        return mvc.perform(get(GET_USER_LIKED_BOARDS_URL).param("page", "0"));
    }
}
