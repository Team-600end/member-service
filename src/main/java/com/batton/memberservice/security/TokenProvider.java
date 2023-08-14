package com.batton.memberservice.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RefreshScope
public class TokenProvider {
    @Value("${token.access-expiration-time}")
    private long ACCESS_TOKEN_EXPIRATION_TIME;
    @Value("${token.refresh-expiration-time}")
    private long REFRESH_TOKEN_EXPIRATION_TIME;
    @Value("${token.secret}")
    private String key;

    public String createAccessToken(String memberId, String uri, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(memberId);
        claims.put("roles", roles);

        return Jwts.builder()
                .addClaims(claims)
                .setExpiration(
                        new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME)
                )
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS512, key)
                .setIssuer(uri)
                .compact();
    }

    public String createRefreshToken() {
        Claims claims = Jwts.claims();
        claims.put("value", UUID.randomUUID());

        return Jwts.builder()
                .addClaims(claims)
                .setExpiration(
                        new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME)
                )
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }

        return false;
    }

    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String getMemberId(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public String getRefreshTokenId(String token) {
        return getClaimsFromToken(token).get("value").toString();
    }

    public Date getExpiredTime(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    public List<String> getRoles(String token) {
        return (List<String>) getClaimsFromToken(token).get("roles");
    }

    public boolean equalRefreshTokenId(String refreshTokenId, String refreshToken) {
        String compareToken = this.getRefreshTokenId(refreshTokenId);
        return refreshTokenId.equals(compareToken);
    }
}
