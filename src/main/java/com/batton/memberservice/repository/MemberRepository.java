package com.batton.memberservice.repository;

import com.batton.memberservice.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    // 이메일로 유저 검색
    Optional<Member> findByEmail(String email);
    // 이메일 중복 확인
    boolean existsByEmail(String email);
}
