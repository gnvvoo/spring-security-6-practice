package com.example.service;

import com.example.dto.LoginRequest;
import com.example.dto.SignupRequest;
import com.example.dto.TokenResponse;
import com.example.entity.User;
import com.example.jwt.JwtProvider;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

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

    public TokenResponse login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("없는 이메일입니다.");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀립니다.");
        }

        String accessToken = jwtProvider.generateToken(user.getEmail());
        String refreshToken = jwtProvider.generateRefreshToken(user.getEmail());

        refreshTokenService.save(user.getEmail(), refreshToken);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String reissue(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다!");
        }

        String email = jwtProvider.getEmail(refreshToken);

        if (!refreshToken.equals(refreshTokenService.find(email))) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다!");
        }

        return jwtProvider.generateToken(email);
    }
}
