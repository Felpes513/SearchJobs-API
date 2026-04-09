package com.searchjobs.api.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserExperienceResponse {
    private Long id;
    private String cargo;
    private String empresa;
    private String descricao;
    private String dataInicio;
    private String dataFim;
}