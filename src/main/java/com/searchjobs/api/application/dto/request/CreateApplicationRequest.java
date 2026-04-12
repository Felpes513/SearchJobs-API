package com.searchjobs.api.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateApplicationRequest {

    @NotNull(message = "jobId é obrigatório")
    private Long jobId;

    private String observacao;
}