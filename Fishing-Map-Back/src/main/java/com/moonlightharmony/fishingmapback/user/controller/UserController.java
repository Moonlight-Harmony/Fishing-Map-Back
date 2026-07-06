package com.moonlightharmony.fishingmapback.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.moonlightharmony.fishingmapback.user.dto.SignUpRequest;
import com.moonlightharmony.fishingmapback.user.dto.SignUpResponse;
import com.moonlightharmony.fishingmapback.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public SignUpResponse signup(@RequestBody SignUpRequest request) {
        return new SignUpResponse(userService.signUp(request));
    }

}
