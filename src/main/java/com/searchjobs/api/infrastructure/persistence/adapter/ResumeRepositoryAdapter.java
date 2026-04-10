package com.searchjobs.api.infrastructure.persistence.adapter;

import com.searchjobs.api.domain.model.Resume;
import com.searchjobs.api.domain.port.out.ResumeRepository;
import com.searchjobs.api.infrastructure.persistence.entity.ResumeJpaEntity;
import com.searchjobs.api.infrastructure.persistence.repository.ResumeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ResumeRepositoryAdapter implements ResumeRepository {

    private final ResumeJpaRepository jpaRepository;

    @Override
    public Resume save(Resume resume) {
        ResumeJpaEntity entity = ResumeJpaEntity.builder()
                .userId(resume.getUserId())
                .fileName(resume.getFileName())
                .filePath(resume.getFilePath())
                .build();

        ResumeJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    private Resume toDomain(ResumeJpaEntity entity) {
        return Resume.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .fileName(entity.getFileName())
                .filePath(entity.getFilePath())
                .extractedText(entity.getExtractedText())
                .parsedJson(entity.getParsedJson())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    @Override
    public Optional<Resume> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Resume update(Resume resume) {
        ResumeJpaEntity entity = jpaRepository.findById(resume.getId())
                .orElseThrow(() -> new RuntimeException("Currículo não encontrado"));

        entity.setFileName(resume.getFileName());
        entity.setFilePath(resume.getFilePath());
        entity.setExtractedText(resume.getExtractedText());
        entity.setParsedJson(resume.getParsedJson());

        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<Resume> findAllByUserId(Long userId) {
        return jpaRepository.findAllByUserId(userId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Page<Resume> findAllByUserId(Long userId, Pageable pageable) {
        return jpaRepository.findAllByUserId(userId, pageable).map(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Optional<Resume> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).map(this::toDomain);
    }
}