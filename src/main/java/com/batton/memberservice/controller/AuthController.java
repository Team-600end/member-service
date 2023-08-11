package com.batton.memberservice.controller;

import com.batton.memberservice.common.BaseResponse;
import com.batton.memberservice.dto.PostEmailCheckReqDTO;
import com.batton.memberservice.dto.PostEmailReqDTO;
import com.batton.memberservice.dto.PostMemberReqDTO;
import com.batton.memberservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    /**
     * 회원가입 API
     * @param postMemberReqDTO 회원 정보 DTO
     * @return String
     */
    @PostMapping("/signup")
    @Operation(summary = "회원가입 요청")
    public BaseResponse<String> signupMember(@RequestBody PostMemberReqDTO postMemberReqDTO) {
        String signupMemberRes = authService.signupMember(postMemberReqDTO);
        log.info("signupMember 요청: " + signupMemberRes);
        return new BaseResponse<>(signupMemberRes);
    }

    /**
     * 메일 발송 API
     * @param postEmailReqDTO
     * @return String
     */
    @PostMapping("/email")
    @Operation(summary = "이메일 검증")
    private BaseResponse<String> emailCheck(@RequestBody PostEmailReqDTO postEmailReqDTO) {
        String result = authService.emailCheck(postEmailReqDTO);

        return new BaseResponse<>(result);
    }

    /**
     * 인증 번호 확인 API
     * @param postEmailCheckReqDTO
     * @return String
     */
    @PostMapping("/email/check")
    @Operation(summary = "인증번호 검증")
    private BaseResponse<String> authCodeCheck(@RequestBody PostEmailCheckReqDTO postEmailCheckReqDTO) {
        String result = authService.authCodeCheck(postEmailCheckReqDTO);

        return new BaseResponse<>(result);
    }
}
