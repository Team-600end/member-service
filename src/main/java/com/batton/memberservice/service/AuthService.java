package com.batton.memberservice.service;

import com.batton.memberservice.common.BaseException;
import com.batton.memberservice.domain.Member;
import com.batton.memberservice.dto.PostEmailCheckReqDTO;
import com.batton.memberservice.dto.PostEmailReqDTO;
import com.batton.memberservice.dto.PostMemberReqDTO;
import com.batton.memberservice.enums.Authority;
import com.batton.memberservice.enums.Status;
import com.batton.memberservice.mq.QueueService;
import com.batton.memberservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

import static com.batton.memberservice.common.BaseResponseStatus.*;
import static com.batton.memberservice.common.ValidationRegex.isRegexEmail;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final QueueService queueService;
    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;

    /**
     * 회원가입 API
     */
    @Transactional
    public String signupMember(PostMemberReqDTO postMemberReqDTO) {
        //이메일 정규 표현 검증
        if (!isRegexEmail(postMemberReqDTO.getEmail())) {
            throw new BaseException(POST_MEMBERS_INVALID_EMAIL);
        }
        // 비밀번호 일치 확인
        if (!postMemberReqDTO.getPassword().equals(postMemberReqDTO.getCheckPassword())) {
            throw new BaseException(MEMBER_PASSWORD_CONFLICT);
        }
        Member member = postMemberReqDTO.toEntity(postMemberReqDTO, passwordEncoder.encode(postMemberReqDTO.getPassword()), Authority.ROLE_USER, Status.ENABLED);

        memberRepository.save(member);
        // 유저 Queue 생성
        queueService.createQueueForMember(member.getId());

        return "회원가입 성공하였습니다.";
    }

    /**
     * 검증을 위한 이메일 발송 API
     */
    public String emailCheck(PostEmailReqDTO postEmailReqDTO) {
        // 이메일 존재 여부 확인
        if (memberRepository.existsByEmail(postEmailReqDTO.getEmail())) {
            throw new BaseException(EXIST_EMAIL_ERROR);
        }

        SimpleMailMessage message = new SimpleMailMessage();
        String authCode = getAuthCode();

        message.setTo(postEmailReqDTO.getEmail());
        message.setSubject("batton 서비스 이메일 인증코드");
        message.setText("인증번호: " + authCode);

        javaMailSender.send(message);
        redisUtil.setDataExpire(postEmailReqDTO.getEmail(), authCode, 60 * 5);

        return "인증 메일이 발송되었습니다.";
    }

    //인증코드 난수 발생
    private String getAuthCode() {
        Random random = new Random();
        StringBuffer buffer = new StringBuffer();
        int num = 0;

        while (buffer.length() < 6) {
            num = random.nextInt(10);
            buffer.append(num);
        }

        return buffer.toString();
    }

    /**
     * 이메일 인증 코드 확인
     */
    public String authCodeCheck(PostEmailCheckReqDTO postEmailCheckReqDTO) {
        if (redisUtil.existData(postEmailCheckReqDTO.getEmail())) {
            if (!redisUtil.getData(postEmailCheckReqDTO.getEmail()).equals(postEmailCheckReqDTO.getAuthCode())) {
                throw new BaseException(INVALID_AUTH_CODE);
            } else {
                return redisUtil.getData(postEmailCheckReqDTO.getEmail());
            }
        } else {
            throw new BaseException(EXPIRE_AUTH_CODE);
        }
    }
}