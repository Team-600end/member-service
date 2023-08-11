package com.batton.memberservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PatchMemberReqDTO {
    private String nickname;
    private String profileImage;

    @Builder
    public PatchMemberReqDTO(String nickname, String profileImage) {
        this.nickname = nickname;
        this.profileImage = profileImage;
    }
}
