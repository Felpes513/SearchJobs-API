package com.searchjobs.api.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplicationResponse {
    private Long id;
    private Long jobId;
    private String titulo;
    private String empresa;
    private String localizacao;
    private String modeloTrabalho;
    private String jobUrl;
    private String status;
    private String observacao;
    private String dataCandidatura;
}