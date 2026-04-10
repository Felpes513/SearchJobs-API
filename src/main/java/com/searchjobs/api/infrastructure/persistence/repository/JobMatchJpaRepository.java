package com.searchjobs.api.infrastructure.persistence.repository;

import com.searchjobs.api.infrastructure.persistence.entity.JobMatchJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface JobMatchJpaRepository extends JpaRepository<JobMatchJpaEntity, Long> {
    List<JobMatchJpaEntity> findAllByUserIdOrderByScoreDesc(Long userId);

    @Transactional
    void deleteByUserIdAndJobId(Long userId, Long jobId);
}