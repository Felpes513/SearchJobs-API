package com.searchjobs.api.domain.model;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserExperience {
    private Long userId;
    private String cargo;
    private String empresa;
    private String descricao;
    private String dataInicio;
    private String dataFim;
}