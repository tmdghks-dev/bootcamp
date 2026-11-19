package com.back.domain.member.member.controller;

import com.back.domain.member.member.service.MemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController                      // HTTP 요청을 받고, 리턴값을 JSON으로 응답
@RequestMapping("/api/v1/members")   // 이 컨트롤러의 모든 주소는 /api/v1/members 로 시작
@RequiredArgsConstructor             // final 필드 생성자 자동 생성 → 스프링이 서비스 주입
@Tag(name = "ApiV1MemberController", description = "API 회원 컨트롤러")  // Swagger 문서용 이름
public class ApiV1MemberController {
    private final MemberService memberService;
}
