package com.carrot.carrotmarketclonecoding.member.helper;

import com.carrot.carrotmarketclonecoding.member.dto.ProfileDto.ProfileUpdateRequestDto;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@TestComponent
public class MemberDtoFactory {

    public MockMultipartFile createProfileImage() {
        return new MockMultipartFile(
                "profileImage",
                "picture.png",
                MediaType.IMAGE_JPEG_VALUE,
                "picture".getBytes());
    }

    public ProfileUpdateRequestDto createProfileUpdateRequestDto() {
        return ProfileUpdateRequestDto.builder()
                .nickname("newNickname")
                .build();
    }

    public MockMultipartFile createProfileUpdateRequest(ProfileUpdateRequestDto profileUpdateRequestDto)
            throws Exception {
        String profileUpdateRequestJson = new ObjectMapper().writeValueAsString(profileUpdateRequestDto);
        return new MockMultipartFile(
                "profileUpdateRequest",
                "profileUpdateRequest.json",
                "application/json",
                profileUpdateRequestJson.getBytes()
        );
    }
}
