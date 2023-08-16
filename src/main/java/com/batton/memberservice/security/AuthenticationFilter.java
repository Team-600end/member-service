package com.batton.memberservice.security;

import com.batton.memberservice.dto.MemberLoginReqDTO;
import com.batton.memberservice.security.service.RefreshTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        Authentication authentication;

        try {
            MemberLoginReqDTO credential = new ObjectMapper().readValue(request.getInputStream(), MemberLoginReqDTO.class);
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credential.getEmail(),
                            credential.getPassword())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return authentication;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        User user = (User)authResult.getPrincipal();
        List<String> roles = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String memberId = user.getUsername();
        String accessToken = tokenProvider.createAccessToken(memberId, request.getRequestURI(), roles);
        Date expiredTime = tokenProvider.getExpiredTime(accessToken);
        String refreshToken = tokenProvider.createRefreshToken();

        refreshTokenService.updateRefreshToken(Long.valueOf(memberId), tokenProvider.getRefreshTokenId(refreshToken));

        TokenDTO.TokenData tokenData = TokenDTO.TokenData.builder().accessToken(accessToken)
                .accessTokenExpiredDate(expiredTime)
                .refreshToken(refreshToken)
                .build();
        TokenDTO tokenDTO = TokenDTO.builder()
                .isSuccess(true)
                .code(200)
                .message("로그인 성공하셨습니다.")
                .result(tokenData)
                        .build();

        response.setContentType(APPLICATION_JSON_VALUE);

        new ObjectMapper().writeValue(response.getOutputStream(), tokenDTO);
        log.info("로그인 : 유저 " + memberId + " 님이 로그인했습니다.");
    }
}
