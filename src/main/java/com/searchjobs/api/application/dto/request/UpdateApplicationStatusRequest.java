package com.searchjobs.api.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateApplicationStatusRequest {

    @NotBlank(message = "Status é obrigatório")
    private String status;
}