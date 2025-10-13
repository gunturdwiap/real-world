package com.example.realworld.controller;

import com.example.realworld.dto.request.LoginRequest;
import com.example.realworld.dto.request.RegisterRequest;
import com.example.realworld.dto.response.UserResponse;
import com.example.realworld.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest registerRequest){
        UserResponse response = authService.register(registerRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @RequestMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        UserResponse response = authService.login(loginRequest);

        return ResponseEntity.ok(response);
    }
}
