package com.searchjobs.api.infrastructure.persistence.repository;

import com.searchjobs.api.infrastructure.persistence.entity.UserProfileJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileJpaRepository extends JpaRepository<UserProfileJpaEntity, Long> {
    Optional<UserProfileJpaEntity> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}