package com.batton.memberservice.security.service;

import com.batton.memberservice.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessTokenService {
    private final TokenProvider tokenProvider;

    public void checkAccessToken(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer", "");

        if (!tokenProvider.validateToken(token)) {
            throw new RuntimeException("Access Token is not Valid");
        }
    }
}
