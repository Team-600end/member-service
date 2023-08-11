package com.batton.memberservice.security;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "refresh_token", timeToLive = 60 * 60 * 24)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {
    @Id
    private String refreshTokenId;
    private Long memberId;

    public static RefreshToken of(Long memberId, String refreshTokenId) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.memberId = memberId;
        refreshToken.refreshTokenId = refreshTokenId;

        return refreshToken;
    }
}
