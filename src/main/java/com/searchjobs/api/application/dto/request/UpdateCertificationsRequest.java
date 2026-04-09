package com.searchjobs.api.application.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class UpdateCertificationsRequest {
    private List<CertificationItemRequest> certificacoes;

    @Getter
    public static class CertificationItemRequest {
        private String nomeCertificacao;
        private String instituicao;
        private String dataObtencao;
    }
}