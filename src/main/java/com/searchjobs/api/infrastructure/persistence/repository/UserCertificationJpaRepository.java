package com.searchjobs.api.infrastructure.persistence.repository;

import com.searchjobs.api.infrastructure.persistence.entity.UserCertificationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCertificationJpaRepository extends JpaRepository<UserCertificationJpaEntity, Long> {
    void deleteByUserId(Long userId);
    List<UserCertificationJpaEntity> findAllByUserId(Long userId);
}