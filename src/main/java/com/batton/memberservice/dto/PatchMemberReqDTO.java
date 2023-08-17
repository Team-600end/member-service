package com.batton.memberservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PatchMemberReqDTO {
    private String nickname;
    private String profileImage;

    @Builder
    public PatchMemberReqDTO(String nickname, String profileImage) {
        this.nickname = nickname;
        this.profileImage = profileImage;
    }
}
