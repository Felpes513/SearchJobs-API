package com.searchjobs.api.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JobResponse {
    private Long id;
    private String externalId;
    private String titulo;
    private String empresa;
    private String localizacao;
    private String modeloTrabalho;
    private String senioridade;
    private String descricao;
    private String salario;
    private String jobUrl;
    private String dataPublicacao;
}