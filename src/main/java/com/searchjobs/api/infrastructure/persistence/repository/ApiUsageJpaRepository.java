package com.searchjobs.api.infrastructure.persistence.repository;

import com.searchjobs.api.infrastructure.persistence.entity.ApiUsageJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ApiUsageJpaRepository extends JpaRepository<ApiUsageJpaEntity, Long> {
    Optional<ApiUsageJpaEntity> findByMesReferencia(String mesReferencia);
}