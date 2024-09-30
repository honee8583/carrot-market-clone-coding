package com.carrot.carrotmarketclonecoding.keyword.controller;

import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.ADD_KEYWORD_SUCCESS;

import com.carrot.carrotmarketclonecoding.auth.dto.LoginUser;
import com.carrot.carrotmarketclonecoding.common.response.ResponseResult;
import com.carrot.carrotmarketclonecoding.keyword.dto.KeywordRequestDto.KeywordCreateRequestDto;
import com.carrot.carrotmarketclonecoding.keyword.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
}
