package com.back.domain.member.member.service;

import com.back.domain.member.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service                   // 스프링이 이 클래스를 "서비스 객체"로 만들어서 관리
@RequiredArgsConstructor   // final 필드를 받는 생성자를 롬복이 자동 생성
public class MemberService {
    private final MemberRepository memberRepository;
}
