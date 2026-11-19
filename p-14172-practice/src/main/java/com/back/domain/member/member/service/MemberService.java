package com.back.domain.member.member.service;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.repository.MemberRepository;
import com.back.global.globalExceptionHandler.MemberDuplicateUsernameException;
import com.back.global.globalExceptionHandler.UnauthenticatedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Member join(String username, String password, String name) {
        memberRepository.findByUsername(username)
                .ifPresent(_ -> {
                    throw new MemberDuplicateUsernameException(username);
                });

        Member member = new Member(username, password, name);

        return memberRepository.save(member);
    }

    public Optional<Member> findLatest() {
        return memberRepository.findTopByOrderByIdDesc();
    }

    public long count() {
        return memberRepository.count();
    }

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public Optional<Member> findById(int id) {
        return memberRepository.findById(id);
    }

    // actorId에 해당하는 회원(요청한 사람)을 찾는다. 없으면 401
    public Member getActor(int actorId) {
        return findById(actorId).orElseThrow(
                UnauthenticatedException::new
        );
    }
}
