package com.searchjobs.api.domain.model;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSkill {
    private Long id;
    private Long userId;
    private String nomeSkill;
    private String nivel;
}