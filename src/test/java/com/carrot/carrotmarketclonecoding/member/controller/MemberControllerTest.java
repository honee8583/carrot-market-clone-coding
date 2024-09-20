package com.carrot.carrotmarketclonecoding.member.controller;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.MEMBER_NOT_FOUND;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.PROFILE_DETAIL_SUCCESS;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.PROFILE_UPDATE_SUCCESS;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrot.carrotmarketclonecoding.auth.config.WithCustomMockUser;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.member.dto.ProfileDto.ProfileDetailResponseDto;
import com.carrot.carrotmarketclonecoding.member.service.impl.MemberServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

@WithCustomMockUser
@WebMvcTest(controllers = MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MemberServiceImpl memberService;

    @Nested
    @DisplayName("프로필 업데이트 테스트")
    class ProfileUpdate {

        @Test
        @DisplayName("성공")
        void updateSuccess() throws Exception {
            // given
            MockMultipartFile profileImage = new MockMultipartFile(
                    "profileImage",
                    "picture.png",
                    MediaType.IMAGE_JPEG_VALUE,
                    "picture".getBytes());

            // when
            doNothing().when(memberService).update(anyLong(), any());

            // then
            mvc.perform(multipart("/profile")
                            .file(profileImage)
                            .param("nickname", "newNickname")
                            .with(request -> {
                                request.setMethod("PATCH");
                                return request;
                            })
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", equalTo(200)))
                    .andExpect(jsonPath("$.result", equalTo(true)))
                    .andExpect(jsonPath("$.message", equalTo(PROFILE_UPDATE_SUCCESS.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }

        @Test
        @DisplayName("실패 - 사용자가 존재하지 않음")
        void updateFailMemberNotFound() throws Exception {
            // given
            MockMultipartFile profileImage = new MockMultipartFile(
                    "profileImage",
                    "picture.png",
                    MediaType.IMAGE_JPEG_VALUE,
                    "picture".getBytes());

            // when
            doThrow(MemberNotFoundException.class).when(memberService).update(anyLong(), any());

            // then
            mvc.perform(multipart("/profile")
                            .file(profileImage)
                            .param("nickname", "newNickname")
                            .with(request -> {
                                request.setMethod("PATCH");
                                return request;
                            })
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status", equalTo(401)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(MEMBER_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }
    }

    @Nested
    @DisplayName("프로필 정보 조회 테스트")
    class ProfileDetail {

        @Test
        @DisplayName("성공")
        void profileDetailSuccess() throws Exception {
            // given
            ProfileDetailResponseDto profileDetail = new ProfileDetailResponseDto("profileUrl", "nickname");

            // when
            when(memberService.detail(anyLong())).thenReturn(profileDetail);

            // then
            mvc.perform(get("/profile"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", equalTo(200)))
                    .andExpect(jsonPath("$.result", equalTo(true)))
                    .andExpect(jsonPath("$.message", equalTo(PROFILE_DETAIL_SUCCESS.getMessage())))
                    .andExpect(jsonPath("$.data.nickname", equalTo("nickname")))
                    .andExpect(jsonPath("$.data.profileUrl", equalTo("profileUrl")));
        }

        @Test
        @DisplayName("실패 - 사용자가 존재하지 않음")
        void profileDetailFailMemberNotFound() throws Exception {
            // given
            // when
            doThrow(MemberNotFoundException.class).when(memberService).detail(anyLong());

            // then
            mvc.perform(get("/profile"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status", equalTo(401)))
                    .andExpect(jsonPath("$.result", equalTo(false)))
                    .andExpect(jsonPath("$.message", equalTo(MEMBER_NOT_FOUND.getMessage())))
                    .andExpect(jsonPath("$.data", equalTo(null)));
        }
    }
}