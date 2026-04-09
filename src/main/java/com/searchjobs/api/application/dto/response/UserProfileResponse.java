package com.searchjobs.api.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileResponse {
    private Long id;
    private String resumoProfissional;
    private String cargoDesejado;
    private String cidade;
    private String estado;
    private String linkedinUrl;
    private String githubUrl;
}