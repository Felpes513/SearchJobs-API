package com.searchjobs.api.application.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class UpdateProjectsRequest {
    private List<ProjectItemRequest> projetos;

    @Getter
    public static class ProjectItemRequest {
        private String nome;
        private String descricao;
        private String stack;
        private String link;
    }
}