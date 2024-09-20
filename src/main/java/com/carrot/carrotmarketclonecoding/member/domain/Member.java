package com.carrot.carrotmarketclonecoding.member.domain;

import com.carrot.carrotmarketclonecoding.common.BaseEntity;
import com.carrot.carrotmarketclonecoding.member.domain.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "members")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nickname;
    private String profileUrl;
    private String town;
    private Boolean isTownAuthenticated;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Long authId;

    public void updateProfile(String nickname, String profileUrl) {
        if (nickname != null && nickname.length() > 0) {
            this.nickname = nickname;
        }
        if (profileUrl != null && profileUrl.length() > 0) {
            this.profileUrl = profileUrl;
        }
    }
}
