package com.searchjobs.api.application.service;

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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResumeService implements ResumeUseCase {

    private final ResumeRepository resumeRepository;

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

        Resume resume = Resume.builder()
                .userId(userId)
                .fileName(originalName)
                .filePath(targetPath.toString())
                .build();

        Resume saved = resumeRepository.save(resume);

        return ResumeResponse.builder()
                .id(saved.getId())
                .fileName(saved.getFileName())
                .filePath(saved.getFilePath())
                .createdAt(saved.getCreatedAt())
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
    public List<ResumeResponse> findAllByUser(Long userId) {
        return resumeRepository.findAllByUserId(userId)
                .stream()
                .map(resume -> ResumeResponse.builder()
                        .id(resume.getId())
                        .fileName(resume.getFileName())
                        .filePath(resume.getFilePath())
                        .createdAt(resume.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    public Page<ResumeResponse> findAllByUser(Long userId, Pageable pageable) {
        return resumeRepository.findAllByUserId(userId, pageable)
                .map(resume -> ResumeResponse.builder()
                        .id(resume.getId())
                        .fileName(resume.getFileName())
                        .filePath(resume.getFilePath())
                        .createdAt(resume.getCreatedAt())
                        .build());
    }
}