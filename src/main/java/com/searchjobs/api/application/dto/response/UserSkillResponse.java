package com.searchjobs.api.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSkillResponse {
    private Long id;
    private String nomeSkill;
    private String nivel;
}