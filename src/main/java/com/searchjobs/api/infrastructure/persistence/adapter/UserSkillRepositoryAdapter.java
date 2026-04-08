package com.searchjobs.api.infrastructure.persistence.adapter;

import com.searchjobs.api.domain.port.out.UserSkillRepository;
import com.searchjobs.api.infrastructure.persistence.entity.UserSkillJpaEntity;
import com.searchjobs.api.infrastructure.persistence.repository.UserSkillJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserSkillRepositoryAdapter implements UserSkillRepository {

    private final UserSkillJpaRepository jpaRepository;

    @Override
    public void saveAll(Long userId, List<String> skills) {
        List<UserSkillJpaEntity> entities = skills.stream()
                .map(skill -> UserSkillJpaEntity.builder()
                        .userId(userId)
                        .nomeSkill(skill)
                        .nivel("NAO_INFORMADO")
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