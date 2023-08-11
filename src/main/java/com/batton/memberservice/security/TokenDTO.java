package com.batton.memberservice.security;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
public class TokenDTO {
    private String accessToken;
    private Date accessTokenExpiredDate;
    private String refreshToken;

    @Builder
    public TokenDTO(String accessToken, Date accessTokenExpiredDate, String refreshToken) {
        this.accessToken = accessToken;
        this.accessTokenExpiredDate = accessTokenExpiredDate;
        this.refreshToken = refreshToken;
    }
}
