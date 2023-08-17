package com.batton.memberservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberLoginReqDTO {
    private String email;
    private String password;
}
