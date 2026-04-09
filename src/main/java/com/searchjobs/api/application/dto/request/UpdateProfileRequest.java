package com.searchjobs.api.application.dto.request;

import lombok.Getter;

@Getter
public class UpdateProfileRequest {
    private String resumoProfissional;
    private String cidade;
    private String estado;
    private String linkedinUrl;
    private String githubUrl;
}