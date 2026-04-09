package com.searchjobs.api.infrastructure.persistence.repository;

import com.searchjobs.api.infrastructure.persistence.entity.UserProjectJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserProjectJpaRepository extends JpaRepository<UserProjectJpaEntity, Long> {
    void deleteByUserId(Long userId);
    List<UserProjectJpaEntity> findAllByUserId(Long userId);
}