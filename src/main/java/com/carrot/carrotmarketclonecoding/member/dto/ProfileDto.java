package com.carrot.carrotmarketclonecoding.member.dto;

import lombok.*;

public class ProfileDto {

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileUpdateRequestDto {
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
