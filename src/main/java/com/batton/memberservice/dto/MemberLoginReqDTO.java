package com.batton.memberservice.dto;

import lombok.Getter;

@Getter
public class MemberLoginReqDTO {
    private String email;
    private String password;
}
