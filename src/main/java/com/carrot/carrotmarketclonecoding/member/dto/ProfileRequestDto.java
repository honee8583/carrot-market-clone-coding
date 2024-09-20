package com.carrot.carrotmarketclonecoding.member.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

public class ProfileRequestDto {

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
}
