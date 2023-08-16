package com.batton.memberservice.security.service;

import com.batton.memberservice.domain.Member;
import com.batton.memberservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> optionalMember = memberRepository.findByEmail(username);

        if (optionalMember.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        Member currentMember = optionalMember.get();
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(currentMember.getAuthority().toString()));

        return new User(String.valueOf(currentMember.getId()),
                currentMember.getPassword(),
                authorities
        );
    }
}
