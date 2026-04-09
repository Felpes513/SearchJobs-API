package com.searchjobs.api.infrastructure.persistence.adapter;

import com.searchjobs.api.domain.model.UserProfile;
import com.searchjobs.api.domain.port.out.UserProfileRepository;
import com.searchjobs.api.infrastructure.persistence.entity.UserProfileJpaEntity;
import com.searchjobs.api.infrastructure.persistence.repository.UserProfileJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserProfileRepositoryAdapter implements UserProfileRepository {

    private final UserProfileJpaRepository jpaRepository;

    @Override
    public UserProfile save(UserProfile profile) {
        UserProfileJpaEntity entity;

        if (profile.getId() != null) {
            entity = jpaRepository.findById(profile.getId())
                    .orElse(new UserProfileJpaEntity());
        } else {
            entity = jpaRepository.findByUserId(profile.getUserId())
                    .orElse(new UserProfileJpaEntity());
        }

        entity.setUserId(profile.getUserId());
        entity.setResumoProfissional(profile.getResumoProfissional());
        entity.setCidade(profile.getCidade());
        entity.setEstado(profile.getEstado());
        entity.setLinkedinUrl(profile.getLinkedinUrl());
        entity.setGithubUrl(profile.getGithubUrl());

        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<UserProfile> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).map(this::toDomain);
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        jpaRepository.deleteByUserId(userId);
    }

    private UserProfile toDomain(UserProfileJpaEntity entity) {
        return UserProfile.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .resumoProfissional(entity.getResumoProfissional())
                .cidade(entity.getCidade())
                .estado(entity.getEstado())
                .linkedinUrl(entity.getLinkedinUrl())
                .githubUrl(entity.getGithubUrl())
                .build();
    }
}