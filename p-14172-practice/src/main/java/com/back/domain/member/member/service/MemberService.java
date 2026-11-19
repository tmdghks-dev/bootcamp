package com.back.domain.member.member.service;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Member join(String username, String password, String name) {
        memberRepository.findByUsername(username)
                .ifPresent(_ -> {
                    throw new IllegalArgumentException(
                            "%s(은)는 이미 사용중인 username 입니다.".formatted(username)
                    );
                });

        Member member = new Member(username, password, name);

        return memberRepository.save(member);
    }
}
