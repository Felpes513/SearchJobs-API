package com.searchjobs.api.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResponse {
    private Long id;
    private String mensagem;
    private String tipo;
    private Boolean lida;
    private String createdAt;
}