package com.carrot.carrotmarketclonecoding.member.controller;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.MEMBER_NOT_FOUND;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.PROFILE_DETAIL_SUCCESS;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.PROFILE_UPDATE_SUCCESS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrot.carrotmarketclonecoding.ControllerTest;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.member.dto.ProfileDto.ProfileDetailResponseDto;
import com.carrot.carrotmarketclonecoding.member.helper.MemberDtoFactory;
import com.carrot.carrotmarketclonecoding.member.helper.MemberTestHelper;
import com.carrot.carrotmarketclonecoding.util.ResultFields;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

class MemberControllerTest extends ControllerTest {

    private MemberTestHelper testHelper;

    @Autowired
    private MemberDtoFactory dtoFactory;

    @BeforeEach
    void setUp() {
        this.testHelper = new MemberTestHelper(mvc, restDocs);
    }

    @Nested
    @DisplayName("프로필 업데이트 테스트")
    class ProfileUpdate {

        private MockMultipartFile profileImage;
        private MockMultipartFile profileUpdateRequest;

        @BeforeEach
        void setUp() throws Exception {
            profileImage = dtoFactory.createProfileImage();
            profileUpdateRequest = dtoFactory.createProfileUpdateRequest(dtoFactory.createProfileUpdateRequestDto());
        }

        @Test
        @DisplayName("성공")
        void updateSuccess() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(PROFILE_UPDATE_SUCCESS.getMessage())
                    .build();

            // when
            doNothing().when(memberService).update(anyLong(), any(), any());

            // then
            testHelper.assertUpdateProfileSuccess(resultFields, profileImage, profileUpdateRequest);
        }

        @Test
        @DisplayName("실패 - 사용자가 존재하지 않음")
        void updateFailMemberNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(MemberNotFoundException.class).when(memberService).update(anyLong(), any(), any());

            // then
           testHelper.assertUpdateProfileFail(resultFields, profileImage, profileUpdateRequest);
        }
    }

    @Nested
    @DisplayName("프로필 정보 조회 테스트")
    class ProfileDetail {

        @Test
        @DisplayName("성공")
        void getProfileDetailSuccess() throws Exception {
            // given
            ProfileDetailResponseDto profileDetail = new ProfileDetailResponseDto("profileUrl", "nickname");
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isOk())
                    .status(200)
                    .result(true)
                    .message(PROFILE_DETAIL_SUCCESS.getMessage())
                    .build();

            // when
            when(memberService.detail(anyLong())).thenReturn(profileDetail);

            // then
            testHelper.assertGetProfileDetailSuccess(resultFields);
        }

        @Test
        @DisplayName("실패 - 사용자가 존재하지 않음")
        void getProfileDetailFailMemberNotFound() throws Exception {
            // given
            ResultFields resultFields = ResultFields.builder()
                    .resultMatcher(status().isUnauthorized())
                    .status(401)
                    .result(false)
                    .message(MEMBER_NOT_FOUND.getMessage())
                    .build();

            // when
            doThrow(MemberNotFoundException.class).when(memberService).detail(anyLong());

            // then
            testHelper.assertGetProfileDetailFail(resultFields);
        }
    }
}