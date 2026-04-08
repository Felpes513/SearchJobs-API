package com.searchjobs.api.infrastructure.persistence.repository;

import com.searchjobs.api.infrastructure.persistence.entity.UserCertificationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCertificationJpaRepository extends JpaRepository<UserCertificationJpaEntity, Long> {
    void deleteByUserId(Long userId);
}