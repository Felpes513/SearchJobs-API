package com.searchjobs.api.infrastructure.persistence.repository;

import com.searchjobs.api.infrastructure.persistence.entity.UserSkillJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserSkillJpaRepository extends JpaRepository<UserSkillJpaEntity, Long> {
    void deleteByUserId(Long userId);
    List<UserSkillJpaEntity> findAllByUserId(Long userId);
}