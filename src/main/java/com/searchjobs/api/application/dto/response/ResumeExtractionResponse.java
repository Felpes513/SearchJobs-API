package com.searchjobs.api.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "Resultado da extração inteligente de dados do currículo")
public class ResumeExtractionResponse {

    @Schema(description = "ID do currículo processado", example = "1")
    private Long resumeId;

    @Schema(description = """
            JSON estruturado com os dados extraídos do currículo. Campos possíveis:
            nome, email, telefone, skills (array), experiencias (array), certificacoes (array), projetos (array).
            """,
            example = """
            {
              "nome": "João Silva",
              "email": "joao@email.com",
              "telefone": "(11) 99999-9999",
              "skills": ["Java", "Spring Boot", "PostgreSQL"],
              "experiencias": [{"cargo": "Dev Backend", "empresa": "Empresa X", "periodo": "2022-2024"}],
              "certificacoes": ["AWS Cloud Practitioner"],
              "projetos": [{"nome": "SearchJobs", "descricao": "API de busca de vagas"}]
            }
            """)
    private String parsedJson;

    @Schema(description = "Mensagem de status da extração", example = "Currículo extraído com sucesso")
    private String mensagem;
    private List<String> camposFaltando;
}