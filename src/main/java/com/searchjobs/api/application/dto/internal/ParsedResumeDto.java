package com.searchjobs.api.application.dto.internal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParsedResumeDto {

    private String nome;
    private String email;
    private String telefone;
    private List<String> skills;
    private List<ExperienciaDto> experiencias;
    private List<CertificacaoDto> certificacoes;
    private List<ProjetoDto> projetos;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExperienciaDto {
        private String cargo;
        private String empresa;
        private String descricao;
        private String dataInicio;
        private String dataFim;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CertificacaoDto {
        private String nome;
        private String instituicao;
        private String dataObtencao;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProjetoDto {
        private String nome;
        private String descricao;
        private String stack;
        private String link;
    }
}