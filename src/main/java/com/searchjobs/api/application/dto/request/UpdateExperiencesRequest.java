package com.searchjobs.api.application.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class UpdateExperiencesRequest {
    private List<ExperienceItemRequest> experiencias;

    @Getter
    public static class ExperienceItemRequest {
        private String cargo;
        private String empresa;
        private String descricao;
        private String dataInicio;
        private String dataFim;
    }
}