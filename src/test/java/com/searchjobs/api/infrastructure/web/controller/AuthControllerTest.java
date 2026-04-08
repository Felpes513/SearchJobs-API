package com.searchjobs.api.infrastructure.web.controller;

import com.searchjobs.api.application.dto.response.AuthResponse;
import com.searchjobs.api.application.dto.response.RegisterResponse;
import com.searchjobs.api.domain.port.in.AuthUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AuthController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private AuthUseCase authUseCase;

    private static final String REGISTER_URL = "/api/auth/register";
    private static final String LOGIN_URL    = "/api/auth/login";

    // ─── register ───────────────────────────────────────────────────────────────

    @Test
    void register_deveRetornar201_quandoDadosValidos() throws Exception {
        when(authUseCase.register(any())).thenReturn(
                RegisterResponse.builder().mensagem("Usuário cadastrado com sucesso").build()
        );

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Felipe","email":"felipe@email.com","senha":"senha123"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensagem").value("Usuário cadastrado com sucesso"));
    }

    @Test
    void register_deveRetornar400_quandoNomeEmBranco() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"","email":"felipe@email.com","senha":"senha123"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_deveRetornar400_quandoEmailInvalido() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Felipe","email":"emailinvalido","senha":"senha123"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_deveRetornar400_quandoSenhaMenorQue6Caracteres() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Felipe","email":"felipe@email.com","senha":"123"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_deveRetornar400_quandoCorpoAusente() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // ─── login ──────────────────────────────────────────────────────────────────

    @Test
    void login_deveRetornar200ComTokens_quandoCredenciaisValidas() throws Exception {
        when(authUseCase.login(any())).thenReturn(
                AuthResponse.builder()
                        .accessToken("access-token")
                        .refreshToken("refresh-token")
                        .build()
        );

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"felipe@email.com","senha":"senha123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    void login_deveRetornar400_quandoEmailEmBranco() throws Exception {
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"","senha":"senha123"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_deveRetornar400_quandoEmailInvalido() throws Exception {
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"emailinvalido","senha":"senha123"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_deveRetornar400_quandoSenhaEmBranco() throws Exception {
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"felipe@email.com","senha":""}
                                """))
                .andExpect(status().isBadRequest());
    }
}