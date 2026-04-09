package com.searchjobs.api.application.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

@Getter
public class UpdateSkillsRequest {
    @NotEmpty(message = "A lista de skills não pode ser vazia")
    private List<String> skills;
}