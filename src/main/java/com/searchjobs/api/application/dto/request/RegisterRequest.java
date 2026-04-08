package com.searchjobs.api.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "Dados para registro de novo usuário")
public class RegisterRequest {

    @Schema(description = "Nome completo do usuário", example = "João Silva")
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @Schema(description = "Endereço de e-mail", example = "joao@email.com")
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @Schema(description = "Senha de acesso (mínimo 6 caracteres)", example = "senha123")
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;
}