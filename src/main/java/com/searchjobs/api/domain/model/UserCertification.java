package com.searchjobs.api.domain.model;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCertification {
    private Long userId;
    private String nomeCertificacao;
    private String instituicao;
    private String dataObtencao;
}