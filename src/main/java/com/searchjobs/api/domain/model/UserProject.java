package com.searchjobs.api.domain.model;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProject {
    private Long id;
    private Long userId;
    private String nome;
    private String descricao;
    private String stack;
    private String link;
}