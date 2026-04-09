package com.searchjobs.api.infrastructure.persistence.adapter;

import com.searchjobs.api.domain.model.UserExperience;
import com.searchjobs.api.domain.port.out.UserExperienceRepository;
import com.searchjobs.api.infrastructure.persistence.entity.UserExperienceJpaEntity;
import com.searchjobs.api.infrastructure.persistence.repository.UserExperienceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserExperienceRepositoryAdapter implements UserExperienceRepository {

    private final UserExperienceJpaRepository jpaRepository;

    @Override
    public void saveAll(List<UserExperience> experiences) {
        List<UserExperienceJpaEntity> entities = experiences.stream()
                .map(e -> UserExperienceJpaEntity.builder()
                        .userId(e.getUserId())
                        .cargo(e.getCargo())
                        .empresa(e.getEmpresa())
                        .descricao(e.getDescricao())
                        .dataInicio(e.getDataInicio())
                        .dataFim(e.getDataFim())
                        .build())
                .toList();
        jpaRepository.saveAll(entities);
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        jpaRepository.deleteByUserId(userId);
    }

    @Override
    public List<UserExperience> findAllByUserId(Long userId) {
        return jpaRepository.findAllByUserId(userId)
                .stream()
                .map(e -> UserExperience.builder()
                        .id(e.getId())
                        .userId(e.getUserId())
                        .cargo(e.getCargo())
                        .empresa(e.getEmpresa())
                        .descricao(e.getDescricao())
                        .dataInicio(e.getDataInicio())
                        .dataFim(e.getDataFim())
                        .build())
                .toList();
    }
}