package com.searchjobs.api.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Tokens de autenticação retornados após login bem-sucedido")
public class AuthResponse {

    @Schema(description = "Token JWT de acesso (válido por 15 minutos)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "Token JWT de renovação (válido por 7 dias)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;
}