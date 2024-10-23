package com.carrot.carrotmarketclonecoding.board.helper.boardlike;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@TestComponent
public class BoardLikeRequestHelper {

    private static final String ADD_BOARD_LIKE_URL = "/board/like/{id}";
    private static final String GET_USER_LIKED_BOARDS_URL = "/board/like";

    public ResultActions requestLikeBoard(MockMvc mvc) throws Exception {
        return mvc.perform(post(ADD_BOARD_LIKE_URL, 1L)
                .with(SecurityMockMvcRequestPostProcessors.csrf()));
    }

    public ResultActions requestGetUserLikedBoards(MockMvc mvc) throws Exception {
        return mvc.perform(get(GET_USER_LIKED_BOARDS_URL).param("page", "0"));
    }
}
