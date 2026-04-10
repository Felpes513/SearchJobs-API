package com.searchjobs.api.infrastructure.persistence.adapter;

import com.searchjobs.api.domain.model.JobMatch;
import com.searchjobs.api.domain.port.out.JobMatchRepository;
import com.searchjobs.api.infrastructure.persistence.entity.JobMatchJpaEntity;
import com.searchjobs.api.infrastructure.persistence.repository.JobMatchJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JobMatchRepositoryAdapter implements JobMatchRepository {

    private final JobMatchJpaRepository jpaRepository;

    @Override
    public JobMatch save(JobMatch match) {
        JobMatchJpaEntity entity = JobMatchJpaEntity.builder()
                .userId(match.getUserId())
                .jobId(match.getJobId())
                .score(match.getScore())
                .justificativa(match.getJustificativa())
                .pontosFortesJson(match.getPontosFortesJson())
                .gapsJson(match.getGapsJson())
                .build();

        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<JobMatch> findAllByUserId(Long userId) {
        return jpaRepository.findAllByUserIdOrderByScoreDesc(userId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteByUserIdAndJobId(Long userId, Long jobId) {
        jpaRepository.deleteByUserIdAndJobId(userId, jobId);
    }

    private JobMatch toDomain(JobMatchJpaEntity entity) {
        return JobMatch.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .jobId(entity.getJobId())
                .score(entity.getScore())
                .justificativa(entity.getJustificativa())
                .pontosFortesJson(entity.getPontosFortesJson())
                .gapsJson(entity.getGapsJson())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}