package com.batton.memberservice.dto.client;

import com.batton.memberservice.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class GetMemberResDTO {
    private String nickname;
    private String profileImage;

    @Builder
    public GetMemberResDTO(String nickname, String profileImage) {
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public static GetMemberResDTO toDTO(Member member) {
        return GetMemberResDTO.builder()
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .build();
    }
}

