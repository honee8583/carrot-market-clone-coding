package com.carrot.carrotmarketclonecoding.member.service;

import com.carrot.carrotmarketclonecoding.member.dto.ProfileDto.ProfileUpdateRequestDto;

public interface MemberService {
    void update(Long authId, ProfileUpdateRequestDto profileUpdateRequestDto);
}
