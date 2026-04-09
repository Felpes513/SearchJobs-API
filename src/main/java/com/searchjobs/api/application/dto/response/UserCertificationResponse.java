package com.searchjobs.api.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCertificationResponse {
    private Long id;
    private String nomeCertificacao;
    private String instituicao;
    private String dataObtencao;
}