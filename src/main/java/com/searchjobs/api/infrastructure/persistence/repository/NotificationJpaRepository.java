package com.searchjobs.api.infrastructure.persistence.repository;

import com.searchjobs.api.infrastructure.persistence.entity.NotificationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationJpaRepository extends JpaRepository<NotificationJpaEntity, Long> {
    List<NotificationJpaEntity> findAllByUserIdOrUserIdIsNullOrderByCreatedAtDesc(Long userId);
    List<NotificationJpaEntity> findAllByUserIdIsNullOrderByCreatedAtDesc();
}