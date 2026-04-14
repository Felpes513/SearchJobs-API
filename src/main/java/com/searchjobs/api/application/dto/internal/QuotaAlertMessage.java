package com.searchjobs.api.application.dto.internal;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotaAlertMessage {
    private int totalRequisicoes;
    private int limite;
    private int restantes;
    private String tipo;
}