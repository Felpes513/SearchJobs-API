package com.searchjobs.api.infrastructure.web.controller;

import com.searchjobs.api.application.dto.request.LoginRequest;
import com.searchjobs.api.application.dto.request.RegisterRequest;
import com.searchjobs.api.application.dto.response.AuthResponse;
import com.searchjobs.api.application.dto.response.RegisterResponse;
import com.searchjobs.api.domain.port.in.AuthUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authUseCase.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authUseCase.login(request));
    }
}