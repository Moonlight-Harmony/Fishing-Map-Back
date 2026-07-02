package com.moonlightharmony.fishingmapback.filter;

import com.moonlightharmony.fishingmapback.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // JWT가 없거나 잘못된 형식이면 다음 필터로 넘김
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            log.warn("유효하지 않은 토큰입니다: {}", token);
            filterChain.doFilter(request, response);
            return;
        }

        Claims claims = jwtUtil.parseClaims(token);
        String userId = claims.getSubject();

        // ✅ 현재 컨텍스트에 인증 정보가 없는 경우에만 등록
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 권한(Role)도 JWT에 담겨 있다면 여기서 꺼내서 넣어주면 됨
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId, null, Collections.emptyList()
                    );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("JWT 인증 성공 - userId: {}", userId);
        }

        filterChain.doFilter(request, response);
    }
}
