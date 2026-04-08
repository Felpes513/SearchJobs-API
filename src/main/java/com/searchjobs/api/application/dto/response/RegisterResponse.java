package com.searchjobs.api.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterResponse {
    private String mensagem;
}