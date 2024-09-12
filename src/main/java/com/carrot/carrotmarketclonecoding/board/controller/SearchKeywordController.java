package com.carrot.carrotmarketclonecoding.board.controller;

import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.*;

import com.carrot.carrotmarketclonecoding.auth.dto.LoginUser;
import com.carrot.carrotmarketclonecoding.board.service.SearchKeywordService;
import com.carrot.carrotmarketclonecoding.common.response.ResponseResult;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchKeywordController {
    private final SearchKeywordService searchKeywordService;

    @GetMapping("/rank")
    public ResponseEntity<?> searchKeywordTopRank() {
        Set<String> keywords = searchKeywordService.getTopSearchKeywords();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, GET_TOP_RANK_SEARCH_KEYWORDS_SUCCESS.getMessage(), keywords));
    }

    @GetMapping("/recent")
    public ResponseEntity<?> getRecentSearchKeywords(@AuthenticationPrincipal LoginUser loginUser) {
        Long authId = Long.parseLong(loginUser.getUsername());
        List<String> keywords = searchKeywordService.getRecentSearchKeywords(authId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, GET_RECENT_SEARCH_KEYWORDS_SUCCESS.getMessage(), keywords));
    }

    @DeleteMapping("/recent")
    public ResponseEntity<?> removeRecentKeyword(@AuthenticationPrincipal LoginUser loginUser, @RequestParam("keyword") String keyword) {
        Long authId = Long.parseLong(loginUser.getUsername());
        searchKeywordService.removeRecentSearchKeyword(authId, keyword);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, REMOVE_RECENT_SEARCH_KEYWORD_SUCCESS.getMessage(), null));
    }

    @DeleteMapping("/recent/all")
    public ResponseEntity<?> removeAllRecentKeywords(@AuthenticationPrincipal LoginUser loginUser) {
        Long authId = Long.parseLong(loginUser.getUsername());
        searchKeywordService.removeAllRecentSearchKeywords(authId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, REMOVE_ALL_RECENT_SEARCH_KEYWORD_SUCCESS.getMessage(), null));
    }
}
