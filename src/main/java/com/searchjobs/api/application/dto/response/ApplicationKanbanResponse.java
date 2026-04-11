package com.searchjobs.api.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class ApplicationKanbanResponse {
    private List<ApplicationResponse> pendente;
    private List<ApplicationResponse> salva;
    private List<ApplicationResponse> emFila;
    private List<ApplicationResponse> candidatado;
    private List<ApplicationResponse> emAnalise;
    private List<ApplicationResponse> entrevista;
    private List<ApplicationResponse> aprovado;
    private List<ApplicationResponse> rejeitado;
    private List<ApplicationResponse> ignorado;
}