package com.searchjobs.api.infrastructure.persistence.repository;

import com.searchjobs.api.infrastructure.persistence.entity.UserSettingsJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSettingsJpaRepository extends JpaRepository<UserSettingsJpaEntity, Long> {
    Optional<UserSettingsJpaEntity> findByUserId(Long userId);
}
