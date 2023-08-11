import com.batton.memberservice.common.BaseException;
import com.batton.memberservice.domain.Member;
import com.batton.memberservice.dto.GetMemberInfoResDTO;
import com.batton.memberservice.dto.PostEmailReqDTO;
import com.batton.memberservice.dto.PostMemberReqDTO;
import com.batton.memberservice.dto.client.GetMemberResDTO;
import com.batton.memberservice.enums.Authority;
import com.batton.memberservice.enums.Status;
import com.batton.memberservice.mq.QueueService;
import com.batton.memberservice.repository.MemberRepository;
import com.batton.memberservice.service.AuthService;
import com.batton.memberservice.service.MemberService;
import com.batton.memberservice.service.ObjectStorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTests {
    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private MemberService memberService;
    @InjectMocks
    private AuthService authService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private QueueService queueService;
    @Mock
    private MultipartFile profileImage;
    @Mock
    private ObjectStorageService objectStorageService;

    @Test
    @DisplayName("유저 회원가입 성공")
    public void testSignupMemberSuccess() {
        // given
        PostMemberReqDTO postMemberReqDTO = new PostMemberReqDTO("test@example.com", "code", "nika", "password", "password");
        when(passwordEncoder.encode(postMemberReqDTO.getPassword())).thenReturn("encoded-password");

        // when
        String result = authService.signupMember(postMemberReqDTO);

        // then
        assertEquals("회원가입 성공하였습니다.", result);
        verify(memberRepository, times(1)).save(any());
        verify(queueService, times(1)).createQueueForMember(any());
    }

    @Test
    @DisplayName("유저 회원가입 시 이메일 정규표현 예외 처리")
    public void testSignupMemberRegexEmail() {
        // given
        PostMemberReqDTO postMemberReqDTO = new PostMemberReqDTO("testexamplecom", "code","nika", "password", "password");

        // when, then
        assertThrows(BaseException.class, () -> authService.signupMember(postMemberReqDTO));
    }

    @Test
    @DisplayName("유저 회원가입 시 비밀번호 일치 예외 처리")
    public void testSignupMemberPasswordConflict() {
        // given
        PostMemberReqDTO postMemberReqDTO = new PostMemberReqDTO("test@example.com", "code","nika", "password", "drowssap");

        // when, then
        assertThrows(BaseException.class, () -> authService.signupMember(postMemberReqDTO));
    }

    @Test
    @DisplayName("이메일 검증 시 이미 존재하는 이메일 예외 처리")
    public void testEmailCheckExistingEmail() {
        // given
        PostEmailReqDTO postEmailReqDTO = new PostEmailReqDTO("test@email.com");
        when(memberRepository.existsByEmail(postEmailReqDTO.getEmail())).thenReturn(true);

        // when, then
        assertThrows(BaseException.class, () -> authService.emailCheck(postEmailReqDTO));
    }

    @Test
    @DisplayName("유저 정보 조회 성공")
    public void testGetMemberSuccess() {
        // given
        Member validMember = new Member(1L, "test@email.com", "nika", "password", Authority.ROLE_USER, "image", Status.ENABLED);
        when(memberRepository.findById(validMember.getId())).thenReturn(Optional.of(validMember));

        // when
        GetMemberResDTO result = memberService.getMember(validMember.getId());

        // then
        assertNotNull(result);
        assertEquals(validMember.getNickname(), result.getNickname());
    }

    @Test
    @DisplayName("유저 정보 조회 시 잘못된 아이디 예외 처리")
    public void testGetMemberInvalidUser() {
        // given
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when, then
        assertThrows(BaseException.class, () -> memberService.getMember(anyLong()));
    }

    @Test
    @DisplayName("유저 정보 조회 시 탈퇴한 상태 예외 처리")
    public void testGetMemberDisabledUser() {
        // given
        Member disabledMember = new Member(1L, "test@email.com", "nika", "password", Authority.ROLE_USER, "image", Status.DISABLED);
        when(memberRepository.findById(disabledMember.getId())).thenReturn(Optional.of(disabledMember));

        // when, then
        assertThrows(BaseException.class, () -> memberService.getMember(disabledMember.getId()));
    }

    @Test
    @DisplayName("유저 정보 수정 성공")
    public void testPatchMemberSuccess() {
        // given
        String imageUrl = "https://example.com/image.jpg";
        Member member = new Member(1L, "test@email.com", "nika", "password", Authority.ROLE_USER, imageUrl, Status.ENABLED);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(objectStorageService.uploadFile(profileImage)).thenReturn(imageUrl);

        // when
        String result = memberService.patchMember(member.getId(), profileImage, member.getNickname());

        // then
        assertEquals(imageUrl, result);
        verify(memberRepository, times(1)).findById(member.getId());
        verify(objectStorageService, times(1)).uploadFile(profileImage);
    }

    @Test
    @DisplayName("유저 정보 수정 시 유저 아이디 예외 처리")
    public void testPatchMemberDisabledUser() {
        // given
        Member disabledMember = new Member(1L, "test@email.com", "nika", "password", Authority.ROLE_USER, "image", Status.DISABLED);
        when(memberRepository.findById(disabledMember.getId())).thenReturn(Optional.of(disabledMember));

        // when, then
        assertThrows(BaseException.class, () -> memberService.patchMember(disabledMember.getId(), profileImage, disabledMember.getNickname()));
    }

    @Test
    @DisplayName("추가할 유저 정보 조회 성공")
    public void testGetCheckMemberSuccess() {
        // given
        Member member = new Member(1L, "test@email.com", "nika", "password", Authority.ROLE_USER, "image", Status.ENABLED);
        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));

        // when
        GetMemberInfoResDTO result = memberService.getCheckMember(member.getEmail());

        // then
        assertNotNull(result);
        assertEquals(member.getNickname(), result.getNickname());
    }

    @Test
    @DisplayName("추가할 유저 정보 조회 시 잘못된 이메일 예외 처리")
    public void testGetCheckMemberInvalidEmail() {
        // given
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // when, then
        assertThrows(BaseException.class, () -> memberService.getCheckMember(anyString()));
    }
}

