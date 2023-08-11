package com.batton.memberservice.dto;

import com.batton.memberservice.domain.Member;
import com.batton.memberservice.enums.Authority;
import com.batton.memberservice.enums.Status;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostMemberReqDTO {
    private String email;
    private String authCode;
    private String nickname;
    private String password;
    private String checkPassword;

    @Builder
    public PostMemberReqDTO(String email, String authCode, String nickname, String password, String checkPassword) {
        this.email = email;
        this.authCode = authCode;
        this.nickname = nickname;
        this.password = password;
        this.checkPassword = checkPassword;
    }

    public static Member toEntity(PostMemberReqDTO postMemberReqDTO, String password, Authority authority, Status status) {
        return Member.builder()
                .email(postMemberReqDTO.getEmail())
                .nickname(postMemberReqDTO.getNickname())
                .password(password)
                .authority(authority)
                .status(status)
                .build();
    }
}
