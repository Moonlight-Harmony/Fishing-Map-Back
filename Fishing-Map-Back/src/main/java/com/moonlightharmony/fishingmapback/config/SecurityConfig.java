package com.moonlightharmony.fishingmapback.config;

import com.moonlightharmony.fishingmapback.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // ✅ JwtAuthenticationFilter를 주입받음
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ✅ CSRF 비활성화 (React와 REST API 통신 시 보통 disable)
                .csrf(csrf -> csrf.disable())

                // ✅ CORS 활성화 (WebConfig에서 설정한 CorsConfigurationSource 적용)
                .cors(cors -> {})

                // ✅ 세션을 사용하지 않는 Stateless 설정 (JWT 기반 인증용)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))


                // ✅ 요청 URL별 인가 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()  // 로그인, 회원가입은 허용
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // ✅ 이 줄을 추가하세요! WebSocket 연결 엔드포인트를 허용해줍니다.
                        .requestMatchers("/ws-chat/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/signup", "/api/auth/login").anonymous()
                        .anyRequest().authenticated()             // 나머지는 인증 필요
                )

                // ✅ JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 이전에 등록
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ✅ 비밀번호 암호화를 위한 PasswordEncoder Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
