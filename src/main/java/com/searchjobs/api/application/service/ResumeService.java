package com.searchjobs.api.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchjobs.api.application.dto.internal.ParsedResumeDto;
import com.searchjobs.api.application.dto.response.ResumeListResponse;
import com.searchjobs.api.application.dto.response.ResumeResponse;
import com.searchjobs.api.domain.exception.InvalidFileException;
import com.searchjobs.api.domain.exception.ResumeNotFoundException;
import com.searchjobs.api.domain.model.Resume;
import com.searchjobs.api.domain.port.in.ResumeUseCase;
import com.searchjobs.api.domain.port.out.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResumeService implements ResumeUseCase {

    private final ResumeRepository resumeRepository;
    private final ObjectMapper objectMapper;

    @Value("${storage.resumes-dir}")
    private String resumesDir;

    @Override
    public ResumeResponse upload(Long userId, MultipartFile file) {
        validateFile(file);

        String originalName = file.getOriginalFilename();
        String uniqueName = UUID.randomUUID() + "_" + originalName;
        Path targetPath = Paths.get(resumesDir).resolve(uniqueName);

        try {
            Files.createDirectories(targetPath.getParent());
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar o arquivo: " + e.getMessage());
        }

        // Verifica se já existe um currículo para esse usuário
        Optional<Resume> existing = resumeRepository.findByUserId(userId);

        Resume resume;
        if (existing.isPresent()) {
            // Atualiza o existente
            resume = Resume.builder()
                    .id(existing.get().getId())
                    .userId(userId)
                    .fileName(originalName)
                    .filePath(targetPath.toString())
                    .build();
            resume = resumeRepository.update(resume);
        } else {
            resume = Resume.builder()
                    .userId(userId)
                    .fileName(originalName)
                    .filePath(targetPath.toString())
                    .build();
            resume = resumeRepository.save(resume);
        }

        return ResumeResponse.builder()
                .id(resume.getId())
                .fileName(resume.getFileName())
                .filePath(resume.getFilePath())
                .createdAt(resume.getCreatedAt())
                .build();
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileException("O arquivo está vazio");
        }
        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.toLowerCase().endsWith(".pdf")) {
            throw new InvalidFileException("Apenas arquivos PDF são permitidos");
        }
    }

    @Override
    public void delete(Long userId, Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResumeNotFoundException(resumeId));

        if (!resume.getUserId().equals(userId)) {
            throw new ResumeNotFoundException(resumeId);
        }

        try {
            Files.deleteIfExists(Paths.get(resume.getFilePath()));
        } catch (IOException e) {
            throw new RuntimeException("Erro ao deletar o arquivo: " + e.getMessage());
        }

        resumeRepository.deleteById(resumeId);
    }

    @Override
    public Page<ResumeListResponse> findAllByUser(Long userId, Pageable pageable) {
        return resumeRepository.findAllByUserId(userId, pageable)
                .map(this::toListResponse);
    }

    private ResumeListResponse toListResponse(Resume resume) {
        if (resume.getParsedJson() == null) {
            return ResumeListResponse.builder()
                    .fileName(resume.getFileName())
                    .createdAt(resume.getCreatedAt())
                    .extraido(false)
                    .build();
        }

        try {
            ParsedResumeDto parsed = objectMapper.readValue(resume.getParsedJson(), ParsedResumeDto.class);
            return ResumeListResponse.builder()
                    .fileName(resume.getFileName())
                    .createdAt(resume.getCreatedAt())
                    .extraido(true)
                    .nome(parsed.getNome())
                    .email(parsed.getEmail())
                    .telefone(parsed.getTelefone())
                    .cidade(parsed.getCidade())
                    .estado(parsed.getEstado())
                    .linkedinUrl(parsed.getLinkedinUrl())
                    .githubUrl(parsed.getGithubUrl())
                    .resumoProfissional(parsed.getResumoProfissional())
                    .skills(parsed.getSkills())
                    .experiencias(mapExperiencias(parsed.getExperiencias()))
                    .certificacoes(mapCertificacoes(parsed.getCertificacoes()))
                    .projetos(mapProjetos(parsed.getProjetos()))
                    .build();
        } catch (Exception e) {
            return ResumeListResponse.builder()
                    .fileName(resume.getFileName())
                    .createdAt(resume.getCreatedAt())
                    .extraido(false)
                    .build();
        }
    }

    private List<ResumeListResponse.ExperienciaResponse> mapExperiencias(List<ParsedResumeDto.ExperienciaDto> list) {
        if (list == null) return null;
        return list.stream()
                .map(e -> ResumeListResponse.ExperienciaResponse.builder()
                        .cargo(e.getCargo())
                        .empresa(e.getEmpresa())
                        .descricao(e.getDescricao())
                        .dataInicio(e.getDataInicio())
                        .dataFim(e.getDataFim())
                        .build())
                .toList();
    }

    private List<ResumeListResponse.CertificacaoResponse> mapCertificacoes(List<ParsedResumeDto.CertificacaoDto> list) {
        if (list == null) return null;
        return list.stream()
                .map(c -> ResumeListResponse.CertificacaoResponse.builder()
                        .nome(c.getNome())
                        .instituicao(c.getInstituicao())
                        .dataObtencao(c.getDataObtencao())
                        .build())
                .toList();
    }

    private List<ResumeListResponse.ProjetoResponse> mapProjetos(List<ParsedResumeDto.ProjetoDto> list) {
        if (list == null) return null;
        return list.stream()
                .map(p -> ResumeListResponse.ProjetoResponse.builder()
                        .nome(p.getNome())
                        .descricao(p.getDescricao())
                        .stack(p.getStack())
                        .link(p.getLink())
                        .build())
                .toList();
    }
}