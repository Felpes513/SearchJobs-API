package com.searchjobs.api.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Schema(description = "Informacoes do curriculo do usuario")
public class ResumeListResponse {

    @Schema(description = "Nome original do arquivo enviado", example = "curriculo-joao.pdf")
    private String fileName;

    @Schema(description = "Data e hora do upload", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Indica se o curriculo ja foi extraido pela IA", example = "true")
    private boolean extraido;

    @Schema(description = "Nome do candidato", example = "Joao Silva")
    private String nome;

    @Schema(description = "E-mail do candidato", example = "joao@email.com")
    private String email;

    @Schema(description = "Telefone do candidato", example = "(11) 99999-9999")
    private String telefone;

    @Schema(description = "Cidade do candidato", example = "Sao Paulo")
    private String cidade;

    @Schema(description = "Estado do candidato", example = "SP")
    private String estado;

    @Schema(description = "URL do LinkedIn", example = "https://linkedin.com/in/joao")
    private String linkedinUrl;

    @Schema(description = "URL do GitHub", example = "https://github.com/joao")
    private String githubUrl;

    @Schema(description = "Resumo profissional", example = "Desenvolvedor Backend com 5 anos de experiencia...")
    private String resumoProfissional;

    @Schema(description = "Lista de habilidades tecnicas", example = "[\"Java\", \"Spring Boot\", \"PostgreSQL\"]")
    private List<String> skills;

    @Schema(description = "Experiencias profissionais")
    private List<ExperienciaResponse> experiencias;

    @Schema(description = "Certificacoes")
    private List<CertificacaoResponse> certificacoes;

    @Schema(description = "Projetos")
    private List<ProjetoResponse> projetos;

    @Getter
    @Builder
    public static class ExperienciaResponse {
        private String cargo;
        private String empresa;
        private String descricao;
        private String dataInicio;
        private String dataFim;
    }

    @Getter
    @Builder
    public static class CertificacaoResponse {
        private String nome;
        private String instituicao;
        private String dataObtencao;
    }

    @Getter
    @Builder
    public static class ProjetoResponse {
        private String nome;
        private String descricao;
        private String stack;
        private String link;
    }
}