package com.searchjobs.api.infrastructure.web.controller;

import com.searchjobs.api.application.dto.response.ResumeExtractionResponse;
import com.searchjobs.api.application.dto.response.ResumeListResponse;
import com.searchjobs.api.application.dto.response.ResumeResponse;
import com.searchjobs.api.domain.port.in.ResumeExtractionUseCase;
import com.searchjobs.api.domain.port.in.ResumeUseCase;
import com.searchjobs.api.infrastructure.security.JwtService;
import com.searchjobs.api.infrastructure.web.handler.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Currículos", description = "Upload e extração inteligente de dados de currículos em PDF")
@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeUseCase resumeUseCase;
    private final JwtService jwtService;
    private final ResumeExtractionUseCase resumeExtractionUseCase;

    @Operation(
            summary = "Enviar currículo",
            description = "Faz o upload de um currículo em formato PDF. Tamanho máximo: 10 MB. Requer autenticação JWT."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",
                    description = "Currículo enviado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
                    description = "Arquivo inválido (não é PDF ou está vazio)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401",
                    description = "Token ausente ou inválido",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "413",
                    description = "Arquivo excede o tamanho máximo de 10 MB",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ResumeResponse>> upload(
            @Parameter(description = "Arquivo PDF do currículo (máx. 10 MB)", required = true)
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Currículo enviado com sucesso", resumeUseCase.upload(userId, file)));
    }

    @Operation(
            summary = "Extrair dados do currículo",
            description = """
                    Extrai e estrutura os dados de um currículo previamente enviado utilizando IA (OpenAI GPT).

                    O JSON retornado em `parsedJson` contém os seguintes campos (quando disponíveis):
                    - `nome`, `email`, `telefone`
                    - `skills` (lista de habilidades)
                    - `experiencias` (lista de experiências profissionais)
                    - `certificacoes` (lista de certificações)
                    - `projetos` (lista de projetos)

                    Requer autenticação JWT.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "Dados extraídos com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401",
                    description = "Token ausente ou inválido",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Currículo não encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "502",
                    description = "Erro ao comunicar com o serviço de IA",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "503",
                    description = "Serviço de IA temporariamente indisponível (rate limit)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/{id}/extract")
    public ResponseEntity<ApiResponse<ResumeExtractionResponse>> extract(
            @Parameter(description = "ID do currículo", required = true) @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Dados extraídos com sucesso", resumeExtractionUseCase.extract(id)));
    }

    @Operation(
            summary = "Listar currículos",
            description = "Retorna os currículos do usuário autenticado com paginação. Ordenados por data de upload (mais recente primeiro). Requer autenticação JWT."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "Lista paginada de currículos",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401",
                    description = "Token ausente ou inválido",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ResumeListResponse>>> findAll(
            @Parameter(description = "Número da página (começa em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de itens por página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(
                ApiResponse.ok("Currículos obtidos com sucesso", resumeUseCase.findAllByUser(userId, pageable))
        );
    }

    @Operation(
            summary = "Deletar currículo",
            description = "Remove um currículo do usuário autenticado. O arquivo físico também é excluído. Requer autenticação JWT."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "Currículo removido com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401",
                    description = "Token ausente ou inválido",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Currículo não encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "ID do currículo", required = true) @PathVariable Long id,
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        resumeUseCase.delete(userId, id);
        return ResponseEntity.ok(ApiResponse.ok("Currículo removido com sucesso"));
    }
}
