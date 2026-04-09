package com.searchjobs.api.infrastructure.persistence.adapter;

import com.searchjobs.api.domain.model.UserCertification;
import com.searchjobs.api.domain.port.out.UserCertificationRepository;
import com.searchjobs.api.infrastructure.persistence.entity.UserCertificationJpaEntity;
import com.searchjobs.api.infrastructure.persistence.repository.UserCertificationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserCertificationRepositoryAdapter implements UserCertificationRepository {

    private final UserCertificationJpaRepository jpaRepository;

    @Override
    public void saveAll(List<UserCertification> certifications) {
        List<UserCertificationJpaEntity> entities = certifications.stream()
                .map(c -> UserCertificationJpaEntity.builder()
                        .userId(c.getUserId())
                        .nomeCertificacao(c.getNomeCertificacao())
                        .instituicao(c.getInstituicao())
                        .dataObtencao(c.getDataObtencao())
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
    public List<UserCertification> findAllByUserId(Long userId) {
        return jpaRepository.findAllByUserId(userId)
                .stream()
                .map(c -> UserCertification.builder()
                        .id(c.getId())
                        .userId(c.getUserId())
                        .nomeCertificacao(c.getNomeCertificacao())
                        .instituicao(c.getInstituicao())
                        .dataObtencao(c.getDataObtencao())
                        .build())
                .toList();
    }
}