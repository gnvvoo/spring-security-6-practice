package com.example.service;

import com.example.dto.SignupRequest;
import com.example.entity.User;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(SignupRequest request) {
        // 1. 이메일 중복 체크
        if (!userRepository.findByEmail(request.getEmail()).isEmpty()) {
            // 중복 알림
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        // 2. 비밀번호 암호화
        String encodedPassword =  passwordEncoder.encode(request.getPassword());

        // 3. User 객체 생성 (Builder)
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .role("ROLE_USER")
                .build();
        // 4. 저장
        userRepository.save(user);
    }
}
