package com.searchjobs.api.infrastructure.persistence.repository;

import com.searchjobs.api.infrastructure.persistence.entity.UserSkillJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSkillJpaRepository extends JpaRepository<UserSkillJpaEntity, Long> {
    void deleteByUserId(Long userId);
}