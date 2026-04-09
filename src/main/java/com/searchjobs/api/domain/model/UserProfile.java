package com.searchjobs.api.domain.model;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    private Long id;
    private Long userId;
    private String resumoProfissional;
    private String cargoDesejado;
    private String cidade;
    private String estado;
    private String linkedinUrl;
    private String githubUrl;
}