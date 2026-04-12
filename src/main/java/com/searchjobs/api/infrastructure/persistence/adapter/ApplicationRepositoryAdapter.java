package com.searchjobs.api.infrastructure.persistence.adapter;

import com.searchjobs.api.domain.model.Application;
import com.searchjobs.api.domain.port.out.ApplicationRepository;
import com.searchjobs.api.infrastructure.persistence.entity.ApplicationJpaEntity;
import com.searchjobs.api.infrastructure.persistence.repository.ApplicationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ApplicationRepositoryAdapter implements ApplicationRepository {

    private final ApplicationJpaRepository jpaRepository;

    @Override
    public Application save(Application application) {
        ApplicationJpaEntity entity = ApplicationJpaEntity.builder()
                .userId(application.getUserId())
                .jobId(application.getJobId())
                .status(application.getStatus())
                .observacao(application.getObservacao())
                .dataCandidatura(application.getDataCandidatura())
                .build();
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<Application> findAllByUserId(Long userId) {
        return jpaRepository.findAllByUserId(userId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Application> findByIdAndUserId(Long id, Long userId) {
        return jpaRepository.findByIdAndUserId(id, userId).map(this::toDomain);
    }

    @Override
    public void delete(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByUserIdAndJobId(Long userId, Long jobId) {
        return jpaRepository.existsByUserIdAndJobId(userId, jobId);
    }

    private Application toDomain(ApplicationJpaEntity entity) {
        return Application.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .jobId(entity.getJobId())
                .status(entity.getStatus())
                .observacao(entity.getObservacao())
                .dataCandidatura(entity.getDataCandidatura())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}