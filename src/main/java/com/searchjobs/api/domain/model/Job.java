package com.searchjobs.api.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    private Long id;
    private String externalId;
    private String titulo;
    private String empresa;
    private String localizacao;
    private String modeloTrabalho;
    private String senioridade;
    private String descricao;
    private String requisitos;
    private String salario;
    private String jobUrl;
    private LocalDateTime dataPublicacao;
    private LocalDateTime createdAt;
}