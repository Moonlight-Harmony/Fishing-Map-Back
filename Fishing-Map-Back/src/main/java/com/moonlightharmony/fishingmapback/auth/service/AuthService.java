package com.moonlightharmony.fishingmapback.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.moonlightharmony.fishingmapback.auth.dto.LoginRequest;
import com.moonlightharmony.fishingmapback.auth.dto.LoginResponse;
import com.moonlightharmony.fishingmapback.exception.AppException;
import com.moonlightharmony.fishingmapback.exception.ErrorCode;
import com.moonlightharmony.fishingmapback.user.dto.UserSummaryResponse;
import com.moonlightharmony.fishingmapback.user.entity.User;
import com.moonlightharmony.fishingmapback.user.repository.UserRepository;
import com.moonlightharmony.fishingmapback.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtUtil.generateToken(String.valueOf(user.getId()));

        return new LoginResponse(accessToken, UserSummaryResponse.from(user));
    }
}
