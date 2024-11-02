package com.carrot.carrotmarketclonecoding.keyword.helper;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;

import com.carrot.carrotmarketclonecoding.keyword.dto.KeywordRequestDto.KeywordCreateRequestDto;
import com.carrot.carrotmarketclonecoding.keyword.dto.KeywordRequestDto.KeywordEditRequestDto;
import com.carrot.carrotmarketclonecoding.util.ControllerRequestUtil;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

public class KeywordRequestHelper extends ControllerRequestUtil {

    private static final String KEYWORD_URL = "/keyword";
    private static final String EDIT_KEYWORD_URL = KEYWORD_URL + "/{id}";

    private final MockMvc mvc;

    public KeywordRequestHelper(MockMvc mvc) {
        this.mvc = mvc;
    }

    public ResultActions requestAddKeyword(KeywordCreateRequestDto requestDto) throws Exception {
        return mvc.perform(requestWithCsrfAndSetContentType(post(KEYWORD_URL), MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto))
        );
    }

    public ResultActions requestGetKeywords() throws Exception {
        return mvc.perform(get(KEYWORD_URL));
    }

    public ResultActions requestEditKeyword(KeywordEditRequestDto editRequestDto) throws Exception {
        return mvc.perform(requestWithCsrfAndSetContentType(patch(EDIT_KEYWORD_URL, 1L), MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(editRequestDto)));
    }

    public ResultActions requestDeleteKeyword() throws Exception {
        return mvc.perform(requestWithCsrf(delete(EDIT_KEYWORD_URL, 1L)));
    }
}
