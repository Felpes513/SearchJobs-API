package com.searchjobs.api.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Confirmação de registro de usuário")
public class RegisterResponse {

    @Schema(description = "Mensagem de confirmação", example = "Usuário registrado com sucesso")
    private String mensagem;
}