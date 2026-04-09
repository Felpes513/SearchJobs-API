package com.searchjobs.api.infrastructure.persistence.repository;

import com.searchjobs.api.infrastructure.persistence.entity.UserExperienceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserExperienceJpaRepository extends JpaRepository<UserExperienceJpaEntity, Long> {
    void deleteByUserId(Long userId);
    List<UserExperienceJpaEntity> findAllByUserId(Long userId);
}