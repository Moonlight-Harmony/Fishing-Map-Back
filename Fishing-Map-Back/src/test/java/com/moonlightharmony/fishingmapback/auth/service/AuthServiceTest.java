package com.moonlightharmony.fishingmapback.auth.service;

import java.lang.reflect.Field;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.moonlightharmony.fishingmapback.auth.dto.LoginRequest;
import com.moonlightharmony.fishingmapback.auth.dto.LoginResponse;
import com.moonlightharmony.fishingmapback.user.entity.User;
import com.moonlightharmony.fishingmapback.user.repository.UserRepository;
import com.moonlightharmony.fishingmapback.util.JwtUtil;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    AuthService authService;

    @Test
    void 로그인_성공() throws Exception {
        LoginRequest request = new LoginRequest("test@test.com", "password");

        User user = User.builder()
                .email("test@test.com")
                .password("encodedPassword")
                .nickname("tester")
                .build();
        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, 1L);

        BDDMockito.given(userRepository.findByEmail("test@test.com"))
                .willReturn(Optional.of(user));
        BDDMockito.given(passwordEncoder.matches("password", "encodedPassword"))
                .willReturn(true);
        BDDMockito.given(jwtUtil.generateToken("1"))
                .willReturn("access-token");

        LoginResponse response = authService.login(request);

        Assertions.assertThat(response.accessToken()).isEqualTo("access-token");
        Assertions.assertThat(response.user().email()).isEqualTo("test@test.com");
        Assertions.assertThat(response.user().nickname()).isEqualTo("tester");
        BDDMockito.verify(jwtUtil).generateToken("1");
    }
}