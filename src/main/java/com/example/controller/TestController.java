package com.example.controller;

import com.example.jwt.JwtProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    private final JwtProvider jwtProvider;

    public TestController(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @GetMapping("/private")  // 인증된 사용자만 접근 가능해야 함
    public String priv() { return "private"; }

    @GetMapping("/auth/token")  // 테스트용 토큰 발급
    public String token() {
        return jwtProvider.generateToken("test@test.com");
    }
}