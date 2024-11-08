package com.carrot.carrotmarketclonecoding.word.helper;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.carrot.carrotmarketclonecoding.util.ControllerRequestUtil;
import com.carrot.carrotmarketclonecoding.word.dto.WordRequestDto;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

public class WordRequestHelper extends ControllerRequestUtil {

    private static final String WORD_URL = "/word";
    private static final String WORD_URL_WITH_PATH_VARIABLE = WORD_URL + "/{id}";

    private final MockMvc mvc;

    public WordRequestHelper(MockMvc mvc) {
        this.mvc = mvc;
    }

    public ResultActions requestAddWord(WordRequestDto wordRequest) throws Exception {
        return mvc.perform(
                requestWithCsrfAndSetContentType(post(WORD_URL), MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(wordRequest)));
    }

    public ResultActions requestGetWords() throws Exception {
        return mvc.perform(get(WORD_URL));
    }

    public ResultActions requestUpdateWord(WordRequestDto wordRequest) throws Exception {
        return mvc.perform(
                requestWithCsrfAndSetContentType(put(WORD_URL_WITH_PATH_VARIABLE, 1L), MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(wordRequest)));
    }

    public ResultActions requestRemoveWord() throws Exception {
        return mvc.perform(requestWithCsrf(delete(WORD_URL_WITH_PATH_VARIABLE, 1L)));
    }
}
