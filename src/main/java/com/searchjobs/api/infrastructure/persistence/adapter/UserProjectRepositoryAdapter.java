package com.searchjobs.api.infrastructure.persistence.adapter;

import com.searchjobs.api.domain.model.UserProject;
import com.searchjobs.api.domain.port.out.UserProjectRepository;
import com.searchjobs.api.infrastructure.persistence.entity.UserProjectJpaEntity;
import com.searchjobs.api.infrastructure.persistence.repository.UserProjectJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserProjectRepositoryAdapter implements UserProjectRepository {

    private final UserProjectJpaRepository jpaRepository;

    @Override
    public void saveAll(List<UserProject> projects) {
        List<UserProjectJpaEntity> entities = projects.stream()
                .map(p -> UserProjectJpaEntity.builder()
                        .userId(p.getUserId())
                        .nome(p.getNome())
                        .descricao(p.getDescricao())
                        .stack(p.getStack())
                        .link(p.getLink())
                        .build())
                .toList();
        jpaRepository.saveAll(entities);
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        jpaRepository.deleteByUserId(userId);
    }
}