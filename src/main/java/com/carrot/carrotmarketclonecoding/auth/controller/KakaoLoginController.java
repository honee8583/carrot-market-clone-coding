package com.carrot.carrotmarketclonecoding.auth.controller;

import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.LOGIN_SUCCESS;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.RECREATE_TOKENS_SUCCESS;

import com.carrot.carrotmarketclonecoding.auth.dto.TokenDto;
import com.carrot.carrotmarketclonecoding.auth.service.LoginService;
import com.carrot.carrotmarketclonecoding.common.response.ResponseResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class KakaoLoginController {

    private final LoginService loginService;

    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code) {
        TokenDto tokens = loginService.login(code);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, LOGIN_SUCCESS.getMessage(), tokens));
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String refreshToken) {
        TokenDto tokens = loginService.refresh(refreshToken);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, RECREATE_TOKENS_SUCCESS.getMessage(), tokens));
    }
}
