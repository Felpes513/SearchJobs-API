package com.searchjobs.api.infrastructure.persistence.repository;

import com.searchjobs.api.infrastructure.persistence.entity.ApplicationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ApplicationJpaRepository extends JpaRepository<ApplicationJpaEntity, Long> {
    List<ApplicationJpaEntity> findAllByUserId(Long userId);
    Optional<ApplicationJpaEntity> findByIdAndUserId(Long id, Long userId);
    boolean existsByUserIdAndJobId(Long userId, Long jobId);
}