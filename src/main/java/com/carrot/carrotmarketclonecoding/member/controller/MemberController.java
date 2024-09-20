package com.carrot.carrotmarketclonecoding.member.controller;

import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.PROFILE_DETAIL_SUCCESS;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.PROFILE_UPDATE_SUCCESS;

import com.carrot.carrotmarketclonecoding.auth.dto.LoginUser;
import com.carrot.carrotmarketclonecoding.common.response.ResponseResult;
import com.carrot.carrotmarketclonecoding.member.dto.ProfileDto.ProfileDetailResponseDto;
import com.carrot.carrotmarketclonecoding.member.dto.ProfileDto.ProfileUpdateRequestDto;
import com.carrot.carrotmarketclonecoding.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PatchMapping
    public ResponseEntity<?> update(@AuthenticationPrincipal LoginUser loginUser, @ModelAttribute ProfileUpdateRequestDto profileUpdateRequestDto) {
        Long authId = Long.parseLong(loginUser.getUsername());
        memberService.update(authId, profileUpdateRequestDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, PROFILE_UPDATE_SUCCESS.getMessage(), null));
    }

    @GetMapping
    public ResponseEntity<?> detail(@AuthenticationPrincipal LoginUser loginUser) {
        Long authId = Long.parseLong(loginUser.getUsername());
        ProfileDetailResponseDto profileDetail = memberService.detail(authId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseResult.success(HttpStatus.OK, PROFILE_DETAIL_SUCCESS.getMessage(), profileDetail));
    }
}
