package com.searchjobs.api.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProjectResponse {
    private Long id;
    private String nome;
    private String descricao;
    private String stack;
    private String link;
}