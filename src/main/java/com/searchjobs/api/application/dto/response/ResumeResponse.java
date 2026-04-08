package com.searchjobs.api.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "Dados do currículo após upload")
public class ResumeResponse {

    @Schema(description = "ID único do currículo", example = "1")
    private Long id;

    @Schema(description = "Nome original do arquivo enviado", example = "curriculo-joao.pdf")
    private String fileName;

    @Schema(description = "Caminho de armazenamento interno do arquivo", example = "uploads/resumes/a1b2c3d4-curriculo-joao.pdf")
    private String filePath;

    @Schema(description = "Data e hora do upload", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
}