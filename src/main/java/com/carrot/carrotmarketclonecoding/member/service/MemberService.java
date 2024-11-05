package com.carrot.carrotmarketclonecoding.member.service;

import com.carrot.carrotmarketclonecoding.member.dto.ProfileDto.ProfileDetailResponseDto;
import com.carrot.carrotmarketclonecoding.member.dto.ProfileDto.ProfileUpdateRequestDto;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {
    void update(Long authId, MultipartFile profileImage, ProfileUpdateRequestDto profileUpdateRequestDto);
    ProfileDetailResponseDto detail(Long authId);
}
