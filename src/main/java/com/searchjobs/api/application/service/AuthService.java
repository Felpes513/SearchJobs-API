package com.searchjobs.api.application.service;

import com.searchjobs.api.application.dto.request.LoginRequest;
import com.searchjobs.api.application.dto.request.RegisterRequest;
import com.searchjobs.api.application.dto.response.AuthResponse;
import com.searchjobs.api.application.dto.response.RegisterResponse;
import com.searchjobs.api.domain.exception.EmailAlreadyExistsException;
import com.searchjobs.api.domain.model.User;
import com.searchjobs.api.domain.port.in.AuthUseCase;
import com.searchjobs.api.domain.port.out.UserRepository;
import com.searchjobs.api.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = User.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senhaHash(passwordEncoder.encode(request.getSenha()))
                .build();

        userRepository.save(user);

        return RegisterResponse.builder()
                .mensagem("Usuário cadastrado com sucesso")
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}