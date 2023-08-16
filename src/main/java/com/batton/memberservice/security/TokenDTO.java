package com.batton.memberservice.security;

import lombok.Builder;
import lombok.Getter;
import java.util.Date;

@Getter
public class TokenDTO {
    private Boolean isSuccess;
    private Integer code;
    private String message;
    private TokenData result;

    @Builder
    public TokenDTO(Boolean isSuccess, Integer code, String message, TokenData result) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
        this.result = result;
    }

    @Getter
    public static class TokenData {
        private String accessToken;
        private Date accessTokenExpiredDate;
        private String refreshToken;

        @Builder
        public TokenData(String accessToken, Date accessTokenExpiredDate, String refreshToken) {
            this.accessToken = accessToken;
            this.accessTokenExpiredDate = accessTokenExpiredDate;
            this.refreshToken = refreshToken;
        }
    }
}
