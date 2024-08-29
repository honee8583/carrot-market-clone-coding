package com.carrot.carrotmarketclonecoding.board.controller;

import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.*;

import com.carrot.carrotmarketclonecoding.board.service.SearchKeywordService;
import com.carrot.carrotmarketclonecoding.common.response.ResponseResult;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SearchKeywordController {
    private final SearchKeywordService searchKeywordService;

    @GetMapping("/search/rank")
    public ResponseEntity<?> searchKeywordTopRank() {
        Set<String> keywords = searchKeywordService.getTopSearchRank();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, GET_TOP_RANK_SEARCH_KEYWORDS_SUCCESS.getMessage(), keywords));
    }
}
