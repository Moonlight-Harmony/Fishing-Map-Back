package com.moonlightharmony.fishingmapback.auth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moonlightharmony.fishingmapback.auth.dto.LoginRequest;
import com.moonlightharmony.fishingmapback.auth.dto.LoginResponse;
import com.moonlightharmony.fishingmapback.auth.service.AuthService;
import com.moonlightharmony.fishingmapback.filter.JwtAuthenticationFilter;
import com.moonlightharmony.fishingmapback.user.dto.UserSummaryResponse;
import com.moonlightharmony.fishingmapback.user.entity.User;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    
    @Autowired
    MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    AuthService authService;

    @Test
    void 로그인_성공() throws Exception {
        LoginRequest request = new LoginRequest("test@test.com", "password");

        LoginResponse response = new LoginResponse(
                "access-token",
                new UserSummaryResponse(1L, "test@test.com", "tester", User.Role.USER)
        );

        BDDMockito.given(authService.login(BDDMockito.any(LoginRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").value("access-token"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.email").value("test@test.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.nickname").value("tester"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.role").value("USER"));
    }
}
