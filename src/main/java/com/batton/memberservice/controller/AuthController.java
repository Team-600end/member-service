package com.batton.memberservice.controller;

import com.batton.memberservice.common.BaseResponse;
import com.batton.memberservice.dto.GetKakaoKeyResDTO;
import com.batton.memberservice.dto.PostEmailCheckReqDTO;
import com.batton.memberservice.dto.PostEmailReqDTO;
import com.batton.memberservice.dto.PostMemberReqDTO;
import com.batton.memberservice.security.TokenDTO;
import com.batton.memberservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    /**
     * 회원가입 API
     *
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
     * 카카오 회원가입 및 가입 여부 확인
     * @param token 접근 토큰
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/kakao/{access-token}")
    @Operation(summary = "카카오 소셜 로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.")
    })
    private BaseResponse<TokenDTO.TokenData> kakaoSignup(@PathVariable("access-token") String token) {
        TokenDTO.TokenData tokenData = authService.kakaoSignup(token);

        return new BaseResponse<>(tokenData);
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

    /**
     * 카카오 키 조회
     * @return GetKakaoKeyResDTO
     * */
    @GetMapping("/kakao/key")
    @Operation(summary = "카카오 키 조회")
    private BaseResponse<GetKakaoKeyResDTO> getKakaoKey() {
        GetKakaoKeyResDTO getKakaoKeyResDTO = authService.getKakaoKey();

        return new BaseResponse<>(getKakaoKeyResDTO);
    }
}
