package com.carrot.carrotmarketclonecoding.member.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

public class ProfileDto {

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileUpdateRequestDto {
        private MultipartFile profileImage;
        private String nickname;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileDetailResponseDto {
        private String profileUrl;
        private String nickname;
    }
}
