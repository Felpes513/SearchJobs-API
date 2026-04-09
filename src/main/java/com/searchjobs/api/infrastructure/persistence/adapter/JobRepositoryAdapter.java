package com.searchjobs.api.infrastructure.persistence.adapter;

import com.searchjobs.api.domain.model.Job;
import com.searchjobs.api.domain.port.out.JobRepository;
import com.searchjobs.api.infrastructure.persistence.entity.JobJpaEntity;
import com.searchjobs.api.infrastructure.persistence.repository.JobJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JobRepositoryAdapter implements JobRepository {

    private final JobJpaRepository jpaRepository;

    private static final Long DEFAULT_SOURCE_ID = 1L;

    @Override
    public Job save(Job job) {
        Optional<JobJpaEntity> existing = jpaRepository.findByExternalId(job.getExternalId());

        JobJpaEntity entity = existing.orElse(JobJpaEntity.builder()
                .externalId(job.getExternalId())
                .sourceId(DEFAULT_SOURCE_ID)
                .build());

        entity.setTitulo(job.getTitulo());
        entity.setEmpresa(job.getEmpresa());
        entity.setLocalizacao(job.getLocalizacao());
        entity.setModeloTrabalho(job.getModeloTrabalho());
        entity.setSenioridade(job.getSenioridade());
        entity.setDescricao(job.getDescricao());
        entity.setSalario(job.getSalario());
        entity.setJobUrl(job.getJobUrl());
        entity.setDataPublicacao(job.getDataPublicacao());

        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Job> findByExternalId(String externalId) {
        return jpaRepository.findByExternalId(externalId).map(this::toDomain);
    }

    @Override
    public List<Job> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    private Job toDomain(JobJpaEntity entity) {
        return Job.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .titulo(entity.getTitulo())
                .empresa(entity.getEmpresa())
                .localizacao(entity.getLocalizacao())
                .modeloTrabalho(entity.getModeloTrabalho())
                .senioridade(entity.getSenioridade())
                .descricao(entity.getDescricao())
                .salario(entity.getSalario())
                .jobUrl(entity.getJobUrl())
                .dataPublicacao(entity.getDataPublicacao())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}