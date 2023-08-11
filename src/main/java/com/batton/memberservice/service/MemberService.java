package com.batton.memberservice.service;

import com.batton.memberservice.common.BaseException;
import com.batton.memberservice.domain.Member;
import com.batton.memberservice.dto.GetMemberInfoResDTO;
import com.batton.memberservice.dto.PatchMemberPasswordReqDTO;
import com.batton.memberservice.dto.client.GetMemberResDTO;
import com.batton.memberservice.enums.Status;
import com.batton.memberservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.Optional;

import static com.batton.memberservice.common.BaseResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectStorageService objectStorageService;

    /**
     * 유저 정보 조회 API(Feign Client)
     * */
    public GetMemberResDTO getMember(Long memberId) {
        Optional<Member> member = memberRepository.findById(memberId);
        GetMemberResDTO getMemberResDTO;

        // 유저 존재 여부 확인
        if (member.isPresent() && member.get().getStatus().equals(Status.ENABLED)) {
            getMemberResDTO = GetMemberResDTO.toDTO(member.get());
        } else {
            throw new BaseException(MEMBER_INVALID_USER_ID);
        }

        return getMemberResDTO;
    }

    /**
     * 유저 정보 조회 API
     * */
    public GetMemberInfoResDTO getMemberInfo(Long memberId) {
        Optional<Member> member = memberRepository.findById(memberId);
        GetMemberInfoResDTO getMemberInfoResDTO;

        // 유저 존재 여부 확인
        if (member.isPresent() && member.get().getStatus().equals(Status.ENABLED)) {
            getMemberInfoResDTO = GetMemberInfoResDTO.toDTO(member.get());
        } else {
            throw new BaseException(MEMBER_INVALID_USER_ID);
        }

        return getMemberInfoResDTO;
    }

    /**
     * 추가할 프로젝트 멤버 정보 조회 API
     * */
    public GetMemberInfoResDTO getCheckMember(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        GetMemberInfoResDTO getMemberInfoResDTO;

        // 유저 존재 여부 확인
        if (member.isPresent() && member.get().getStatus().equals(Status.ENABLED)) {
            getMemberInfoResDTO = GetMemberInfoResDTO.toDTO(member.get());
        } else {
            throw new BaseException(MEMBER_INVALID_USER_ID);
        }

        return getMemberInfoResDTO;
    }

    /**
     * 유저 정보 수정 API
     * */
    public String patchMember(Long memberId, MultipartFile profileImage, String nickname) {
        Optional<Member> member = memberRepository.findById(memberId);
        String url;
        // 유저 존재 여부 확인
        if (member.isPresent() && member.get().getStatus().equals(Status.ENABLED)) {
            url = objectStorageService.uploadFile(profileImage);
            member.get().update(nickname, url);
        } else {
            throw new BaseException(MEMBER_INVALID_USER_ID);
        }

        return url;
    }

    /**
     * 유저 비밀번호 수정 API
     * */
    public String patchMemberPassword(Long memberId, PatchMemberPasswordReqDTO patchMemberPasswordReqDTO) {
        Optional<Member> member = memberRepository.findById(memberId);

        // 유저 존재 여부 확인
        if (member.isPresent() && member.get().getStatus().equals(Status.ENABLED)) {
            // 비밀번호 일치 여부 확인
            if (!passwordEncoder.matches(patchMemberPasswordReqDTO.getCurrentPassword(), member.get().getPassword())) {
                throw new BaseException(MEMBER_PASSWORD_DISCORD);
            }
            // 비밀번호 2차 확인
            if (!patchMemberPasswordReqDTO.getChangedPassword().equals(patchMemberPasswordReqDTO.getCheckChangedPassword())) {
                throw new BaseException(MEMBER_PASSWORD_CONFLICT);
            }
            member.get().updatePassword(passwordEncoder.encode(patchMemberPasswordReqDTO.getChangedPassword()));
        } else {
            throw new BaseException(MEMBER_INVALID_USER_ID);
        }

        return "회원 비밀번호 수정되었습니다.";
    }
}
