package com.searchjobs.api.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class JobMatchResponse {
    private Long jobId;
    private String titulo;
    private String empresa;
    private String localizacao;
    private String modeloTrabalho;
    private String jobUrl;
    private Double score;
    private String justificativa;
    private List<String> pontosFortres;
    private List<String> gaps;
}