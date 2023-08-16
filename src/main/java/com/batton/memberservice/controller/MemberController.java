package com.batton.memberservice.controller;

import com.batton.memberservice.common.BaseResponse;
import com.batton.memberservice.dto.GetMemberIdResDTO;
import com.batton.memberservice.dto.GetMemberInfoResDTO;
import com.batton.memberservice.dto.PatchMemberPasswordReqDTO;
import com.batton.memberservice.dto.client.GetMemberResDTO;
import com.batton.memberservice.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    /**
     * 유저 정보 조회 API(Feign Client)
     * @param memberId 정보를 조회할 유저 아이디
     * @return GetMemberResDTO
     * */
    @GetMapping("/{memberId}")
    @Operation(summary = "유저 정보 조회 feign client")
    @ApiResponses({
            @ApiResponse(responseCode = "600", description = "유저 아이디 값을 확인해주세요.")
    })
    private GetMemberResDTO getMember(@PathVariable("memberId") Long memberId) {
        GetMemberResDTO getMemberResDTO = memberService.getMember(memberId);

        return getMemberResDTO;
    }

    /**
     * 유저 정보 조회 API
     * @param memberId 정보를 조회할 유저 아이디
     * @return GetMemberResDTO
     * */
    @GetMapping
    @Operation(summary = "유저 정보 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "600", description = "유저 아이디 값을 확인해주세요.")
    })
    private BaseResponse<GetMemberInfoResDTO> getMemberInfo(@RequestHeader Long memberId) {
        GetMemberInfoResDTO getMemberInfoResDTO = memberService.getMemberInfo(memberId);

        return new BaseResponse<>(getMemberInfoResDTO);
    }

    /**
     * 추가할 멤버 정보 조회 API
     * @param email 정보를 조회할 유저 이메일
     * @return GetMemberInfoResDTO
     */
    @GetMapping("/list/{email}")
    @Operation(summary = "추가할 멤버 정보 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "600", description = "유저 아이디 값을 확인해주세요.")
    })
    private BaseResponse<GetMemberInfoResDTO> getCheckMember(@PathVariable("email") String email) {
        GetMemberInfoResDTO getMemberInfoResDTO = memberService.getCheckMember(email);

        return new BaseResponse<>(getMemberInfoResDTO);
    }

    /**
     * 유저 정보 수정 API
     * @param memberId 정보를 수정할 유저 아이디
     * @param profileImage 변경할 이미지
     * @param nickname 변경할 닉네임
     * @return String
     * */
    @PatchMapping
    @Operation(summary = "유저 정보 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "600", description = "유저 아이디 값을 확인해주세요.")
    })
    private BaseResponse<String> patchMember(@RequestHeader Long memberId, @RequestPart(value = "profileImg", required = false) MultipartFile profileImage,
                                             @RequestPart(value = "nickname", required = false) String nickname) {
        String patchMemberRes = memberService.patchMember(memberId, profileImage, nickname);

        return new BaseResponse<>(patchMemberRes);
    }

    /**
     * 유저 비밀번호 수정 API
     * @param memberId 비밀번호를 수정할 유저 아이디
     * @param patchMemberPasswordReqDTO 비밀번호 수정 요청 바디에 포함될 DTO
     * @return String
     * */
    @PatchMapping("/password")
    @Operation(summary = "유저 비밀번호 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "600", description = "유저 아이디 값을 확인해주세요."),
            @ApiResponse(responseCode = "602", description = "두 비밀번호를 같게 입력해주세요."),
            @ApiResponse(responseCode = "603", description = "비밀번호가 일치하지 않습니다.")

    })
    private BaseResponse<String> patchMemberPassword(@RequestHeader Long memberId, @RequestBody PatchMemberPasswordReqDTO patchMemberPasswordReqDTO) {
        String patchMemberPasswordRes = memberService.patchMemberPassword(memberId, patchMemberPasswordReqDTO);

        return new BaseResponse<>(patchMemberPasswordRes);
    }

    /**
     * 멤버 아이디 조회 API
     * @param memberId
     * @return memberId
     */
    @GetMapping("/id")
    @Operation(summary = "멤버 아이디(memberId) 요청")
    private BaseResponse<GetMemberIdResDTO> getMemberId(@RequestHeader Long memberId) {
        GetMemberIdResDTO getMemberIdResDTO = new GetMemberIdResDTO(memberId);

        return new BaseResponse<>(getMemberIdResDTO);
    }

    /**
     * 멤버 탈퇴하기 API
     * @param memberId 탈퇴할 멤버 아이디
     * @return String
     */
}
