package com.moonlightharmony.fishingmapback.user.service;

import java.lang.reflect.Field;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.moonlightharmony.fishingmapback.exception.AppException;
import com.moonlightharmony.fishingmapback.exception.ErrorCode;
import com.moonlightharmony.fishingmapback.user.dto.SignUpRequest;
import com.moonlightharmony.fishingmapback.user.entity.User;
import com.moonlightharmony.fishingmapback.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @Test
    void 회원가입_성공() {
        SignUpRequest request = new SignUpRequest(
            "test@test.com",
            "password",
            "tester"
        );

        BDDMockito.given(userRepository.existsByEmail("test@test.com")).willReturn(false);
        BDDMockito.given(userRepository.existsByNickname("tester")).willReturn(false);
        BDDMockito.given(passwordEncoder.encode("password")).willReturn("encodedPassword");
        BDDMockito.given(userRepository.save(BDDMockito.any()))
            .willAnswer(invocation -> {
                User user =
                    invocation.getArgument(0);
                Field idField = user.getClass().getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(user, 1L);
                return user;
            });

        long userId = userService.signUp(request);

        Assertions.assertThat(userId).isEqualTo(1L);
        BDDMockito.verify(userRepository).save(BDDMockito.any(User.class));
    }

    @Test
    void 이메일이_중복되면_회원가입에_실패한다() {
        BDDMockito.given(userRepository.existsByEmail("test@test.com")).willReturn(true);

        SignUpRequest request = new SignUpRequest(
                "test@test.com",
                "password",
                "tester"
        );

        Assertions.assertThatThrownBy(() -> userService.signUp(request))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    @Test
    void 닉네임이_중복되면_회원가입에_실패한다() {
        BDDMockito.given(userRepository.existsByNickname("tester")).willReturn(true);

        SignUpRequest request = new SignUpRequest(
                "test@test.com",
                "password",
                "tester"
        );

        Assertions.assertThatThrownBy(() -> userService.signUp(request))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.NICKNAME_ALREADY_EXISTS);
    }
}
