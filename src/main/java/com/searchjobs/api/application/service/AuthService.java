package com.searchjobs.api.application.service;

import com.searchjobs.api.application.dto.request.ForgotPasswordRequest;
import com.searchjobs.api.application.dto.request.LoginRequest;
import com.searchjobs.api.application.dto.request.RegisterRequest;
import com.searchjobs.api.application.dto.request.ResetPasswordRequest;
import com.searchjobs.api.application.dto.response.AuthResponse;
import com.searchjobs.api.application.dto.response.RegisterResponse;
import com.searchjobs.api.domain.exception.EmailAlreadyExistsException;
import com.searchjobs.api.domain.model.User;
import com.searchjobs.api.domain.port.in.AuthUseCase;
import com.searchjobs.api.domain.port.out.UserRepository;
import com.searchjobs.api.infrastructure.mail.EmailService;
import com.searchjobs.api.infrastructure.persistence.entity.PasswordResetTokenJpaEntity;
import com.searchjobs.api.infrastructure.persistence.repository.PasswordResetTokenJpaRepository;
import com.searchjobs.api.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetTokenJpaRepository passwordResetTokenRepository;
    private final EmailService emailService;

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
    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    passwordResetTokenRepository.deleteByUserId(user.getId());

                    String token = UUID.randomUUID().toString();

                    PasswordResetTokenJpaEntity entity = PasswordResetTokenJpaEntity.builder()
                            .userId(user.getId())
                            .token(token)
                            .expiresAt(LocalDateTime.now().plusMinutes(30))
                            .build();

                    passwordResetTokenRepository.save(entity);
                    emailService.sendPasswordResetEmail(user.getEmail(), token);
                });
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetTokenJpaEntity tokenEntity = passwordResetTokenRepository
                .findByToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Token inválido ou expirado"));

        if (tokenEntity.getUsed()) {
            throw new IllegalArgumentException("Token já utilizado");
        }

        if (tokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expirado");
        }

        User user = userRepository.findById(tokenEntity.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        User updated = User.builder()
                .id(user.getId())
                .nome(user.getNome())
                .email(user.getEmail())
                .senhaHash(passwordEncoder.encode(request.getNovaSenha()))
                .createdAt(user.getCreatedAt())
                .build();

        userRepository.save(updated);

        tokenEntity.setUsed(true);
        passwordResetTokenRepository.save(tokenEntity);
    }
}