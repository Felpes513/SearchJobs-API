package com.searchjobs.api.infrastructure.persistence.repository;

import com.searchjobs.api.infrastructure.persistence.entity.JobJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobJpaRepository extends JpaRepository<JobJpaEntity, Long> {
    Optional<JobJpaEntity> findByExternalId(String externalId);
}