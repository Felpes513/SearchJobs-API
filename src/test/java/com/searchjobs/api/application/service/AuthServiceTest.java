package com.searchjobs.api.application.service;

import com.searchjobs.api.application.dto.request.LoginRequest;
import com.searchjobs.api.application.dto.request.RegisterRequest;
import com.searchjobs.api.application.dto.response.AuthResponse;
import com.searchjobs.api.application.dto.response.RegisterResponse;
import com.searchjobs.api.domain.exception.EmailAlreadyExistsException;
import com.searchjobs.api.domain.model.User;
import com.searchjobs.api.domain.port.out.UserRepository;
import com.searchjobs.api.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks private AuthService authService;

    // ─── register ───────────────────────────────────────────────────────────────

    @Test
    void register_deveRetornarMensagemDeSucesso_quandoEmailNaoExiste() {
        RegisterRequest request = buildRegisterRequest("Felipe", "felipe@email.com", "senha123");
        when(userRepository.existsByEmail("felipe@email.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("$2a$hash");

        RegisterResponse response = authService.register(request);

        assertThat(response.getMensagem()).isEqualTo("Usuário cadastrado com sucesso");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_deveEncodarSenha_antesDeSerSalva() {
        RegisterRequest request = buildRegisterRequest("Felipe", "felipe@email.com", "senha123");
        when(userRepository.existsByEmail("felipe@email.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("$2a$hash");

        authService.register(request);

        verify(passwordEncoder).encode("senha123");
        verify(userRepository).save(argThat(user -> "$2a$hash".equals(user.getSenhaHash())));
    }

    @Test
    void register_deveLancarEmailAlreadyExistsException_quandoEmailJaCadastrado() {
        RegisterRequest request = buildRegisterRequest("Felipe", "felipe@email.com", "senha123");
        when(userRepository.existsByEmail("felipe@email.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("felipe@email.com");

        verify(userRepository, never()).save(any());
    }

    // ─── login ──────────────────────────────────────────────────────────────────

    @Test
    void login_deveRetornarAccessERefreshToken_quandoCredenciaisValidas() {
        LoginRequest request = buildLoginRequest("felipe@email.com", "senha123");
        User user = User.builder().email("felipe@email.com").build();
        when(userRepository.findByEmail("felipe@email.com")).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken("felipe@email.com")).thenReturn("access-token");
        when(jwtService.generateRefreshToken("felipe@email.com")).thenReturn("refresh-token");

        AuthResponse response = authService.login(request);

        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
    }

    @Test
    void login_deveLancarExcecao_quandoCredenciaisInvalidas() {
        LoginRequest request = buildLoginRequest("felipe@email.com", "senhaErrada");
        doThrow(new BadCredentialsException("Credenciais inválidas"))
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);

        verify(userRepository, never()).findByEmail(any());
    }

    // ─── helpers ────────────────────────────────────────────────────────────────

    private RegisterRequest buildRegisterRequest(String nome, String email, String senha) {
        RegisterRequest request = new RegisterRequest();
        ReflectionTestUtils.setField(request, "nome", nome);
        ReflectionTestUtils.setField(request, "email", email);
        ReflectionTestUtils.setField(request, "senha", senha);
        return request;
    }

    private LoginRequest buildLoginRequest(String email, String senha) {
        LoginRequest request = new LoginRequest();
        ReflectionTestUtils.setField(request, "email", email);
        ReflectionTestUtils.setField(request, "senha", senha);
        return request;
    }
}