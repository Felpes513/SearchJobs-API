package com.searchjobs.api.infrastructure.web.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Estrutura padrão de resposta de erro")
public class ErrorResponse {

    @Schema(description = "Código HTTP do erro", example = "400")
    private int status;

    @Schema(description = "Descrição resumida do erro", example = "Bad Request")
    private String erro;

    @Schema(description = "Mensagem detalhada do erro", example = "Arquivo inválido. Apenas PDFs são aceitos.")
    private String mensagem;

    @Schema(description = "Caminho da requisição que gerou o erro", example = "/api/resumes/upload")
    private String path;

    @Schema(description = "Data e hora do erro", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Mapa de erros por campo (presente apenas em erros de validação)",
            example = "{\"email\": \"Email inválido\", \"senha\": \"Senha deve ter no mínimo 6 caracteres\"}")
    private Map<String, String> campos;
}