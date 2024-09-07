package com.carrot.carrotmarketclonecoding.auth.dto;

import com.carrot.carrotmarketclonecoding.member.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginRespDto {
    private Long authId;
    private Role role;
}
