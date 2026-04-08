package com.searchjobs.api.infrastructure.persistence.repository;

import com.searchjobs.api.infrastructure.persistence.entity.ResumeJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeJpaRepository extends JpaRepository<ResumeJpaEntity, Long> {
    List<ResumeJpaEntity> findAllByUserId(Long userId);
    Page<ResumeJpaEntity> findAllByUserId(Long userId, Pageable pageable);
}