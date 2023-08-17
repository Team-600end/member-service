package com.batton.memberservice.dto;

import com.batton.memberservice.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class GetMemberInfoResDTO {
    private Long memberId;
    private String nickname;
    private String profileImage;
    private String email;

    @Builder
    public GetMemberInfoResDTO(Long memberId, String nickname, String profileImage, String email) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.email = email;
    }

    public static GetMemberInfoResDTO toDTO(Member member) {
        return GetMemberInfoResDTO.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .email(member.getEmail())
                .build();
    }
}
