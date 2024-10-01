package com.carrot.carrotmarketclonecoding.keyword.controller;

import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.ADD_KEYWORD_SUCCESS;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.DELETE_KEYWORDS_SUCCESS;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.EDIT_KEYWORD_SUCCESS;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.GET_KEYWORDS_SUCCESS;

import com.carrot.carrotmarketclonecoding.auth.dto.LoginUser;
import com.carrot.carrotmarketclonecoding.common.response.ResponseResult;
import com.carrot.carrotmarketclonecoding.keyword.dto.KeywordRequestDto.KeywordCreateRequestDto;
import com.carrot.carrotmarketclonecoding.keyword.dto.KeywordRequestDto.KeywordEditRequestDto;
import com.carrot.carrotmarketclonecoding.keyword.dto.KeywordResponseDto.KeywordDetailResponseDto;
import com.carrot.carrotmarketclonecoding.keyword.service.KeywordService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;

    @PostMapping("/keyword")
    public ResponseEntity<?> add(@AuthenticationPrincipal LoginUser loginUser,
                                 @RequestBody KeywordCreateRequestDto createRequestDto) {
        keywordService.add(Long.parseLong(loginUser.getUsername()), createRequestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseResult.success(HttpStatus.CREATED, ADD_KEYWORD_SUCCESS.getMessage(), null));
    }

    @PatchMapping("/keyword/{id}")
    public ResponseEntity<?> edit(@AuthenticationPrincipal LoginUser loginUser,
                                  @PathVariable("id") Long keywordId,
                                  @RequestBody KeywordEditRequestDto editRequestDto) {
        keywordService.edit(Long.parseLong(loginUser.getUsername()), keywordId, editRequestDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, EDIT_KEYWORD_SUCCESS.getMessage(), null));
    }

    @GetMapping("/keyword")
    public ResponseEntity<?> getKeywords(@AuthenticationPrincipal LoginUser loginUser) {
        List<KeywordDetailResponseDto> keywords = keywordService.getAllKeywords(
                Long.parseLong(loginUser.getUsername()));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, GET_KEYWORDS_SUCCESS.getMessage(), keywords));
    }

    @DeleteMapping("/keyword/{id}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal LoginUser loginUser,
                                    @PathVariable("id") Long keywordId) {
        keywordService.delete(Long.parseLong(loginUser.getUsername()), keywordId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, DELETE_KEYWORDS_SUCCESS.getMessage(), null));
    }
}
