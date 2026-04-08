package com.searchjobs.api.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "Credenciais de autenticação")
public class LoginRequest {

    @Schema(description = "Endereço de e-mail cadastrado", example = "joao@email.com")
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @Schema(description = "Senha de acesso", example = "senha123")
    @NotBlank(message = "Senha é obrigatória")
    private String senha;
}