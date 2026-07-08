package com.moonlightharmony.fishingmapback.user.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.moonlightharmony.fishingmapback.user.entity.User;

@DataJpaTest
class UserRepositoryTest {
    
    @Autowired
    UserRepository userRepository;

    @Test
    void 이메일이_존재하면_true를_반환한다() {
        User user = User.builder()
                .email("test@test.com")
                .password("password")
                .nickname("tester")
                .build();

        userRepository.save(user);

        boolean exists = userRepository.existsByEmail("test@test.com");

        Assertions.assertThat(exists).isTrue();
    }

    @Test
    void 닉네임이_존재하면_true를_반환한다() {
        User user = User.builder()
                .email("test@test.com")
                .password("password")
                .nickname("tester")
                .build();

        userRepository.save(user);

        boolean exists = userRepository.existsByNickname("tester");

        Assertions.assertThat(exists).isTrue();
    }

    @Test
    void 이메일로_사용자를_찾을수있다() {
        User user = User.builder()
                .email("test@test.com")
                .password("password")
                .nickname("tester")
                .build();

        userRepository.save(user);

        Assertions.assertThat(userRepository.findByEmail("test@test.com")).isPresent();
        Assertions.assertThat(userRepository.findByEmail("test@test.com").get().getEmail()).isEqualTo("test@test.com");
    }
}
