package com.carrot.carrotmarketclonecoding.member.service.impl;

import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.file.service.FileService;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.dto.ProfileDto.ProfileDetailResponseDto;
import com.carrot.carrotmarketclonecoding.member.dto.ProfileDto.ProfileUpdateRequestDto;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import com.carrot.carrotmarketclonecoding.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final FileService fileService;

    @Override
    public void update(Long authId, MultipartFile profileImage, ProfileUpdateRequestDto profileUpdateRequestDto) {
        Member member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);

        fileService.deleteUploadedImage(member.getProfileUrl());
        String profileUrl = null;
        if (profileImage != null) {
            profileUrl = fileService.uploadImage(profileImage);
        }

        log.debug("new nickname: {}", profileUpdateRequestDto.getNickname());
        log.debug("profileUrl: {}", profileUrl);

        member.updateProfile(profileUpdateRequestDto.getNickname(), profileUrl);
        memberRepository.save(member);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileDetailResponseDto detail(Long authId) {
        Member member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
        return new ProfileDetailResponseDto(member.getProfileUrl(), member.getNickname());
    }
}
