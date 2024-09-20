package com.carrot.carrotmarketclonecoding.member.service.impl;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.MEMBER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.file.service.impl.FileServiceImpl;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.dto.ProfileRequestDto.ProfileUpdateRequestDto;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FileServiceImpl fileService;

    @InjectMocks
    private MemberServiceImpl memberService;

    @Nested
    @DisplayName("프로필 업데이트 테스트")
    class ProfileUpdate {

        @Test
        @DisplayName("성공")
        void updateSuccess() {
            // given
            Member mockMember = Member.builder()
                    .authId(1111L)
                    .nickname("oldNickname")
                    .profileUrl("oldProfileUrl")
                    .build();

            ProfileUpdateRequestDto profileUpdateRequestDto = ProfileUpdateRequestDto.builder()
                    .nickname("newNickname")
                    .profileImage(new MockMultipartFile(
                            "file",
                            "file.png",
                            "text/png",
                            ("Picture").getBytes()))
                    .build();

            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));
            doNothing().when(fileService).deleteUploadedImage(anyString());
            when(fileService.uploadImage(any())).thenReturn("newProfileUrl");

            ArgumentCaptor<Member> argumentCaptor = ArgumentCaptor.forClass(Member.class);

            // when
            memberService.update(1111L, profileUpdateRequestDto);

            // then
            verify(memberRepository).save(argumentCaptor.capture());
            Member member = argumentCaptor.getValue();
            assertThat(member.getAuthId()).isEqualTo(1111L);
            assertThat(member.getNickname()).isEqualTo("newNickname");
            assertThat(member.getProfileUrl()).isEqualTo("newProfileUrl");
        }

        @Test
        @DisplayName("실패 - 사용자가 존재하지 않음")
        void updateFailMemberNotFound() {
            // given
            when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> memberService.update(1111L, new ProfileUpdateRequestDto()))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }
    }
}