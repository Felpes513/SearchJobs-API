package com.searchjobs.api.application.service;

import com.searchjobs.api.application.dto.response.NotificationResponse;
import com.searchjobs.api.infrastructure.persistence.repository.NotificationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationJpaRepository notificationJpaRepository;

    public List<NotificationResponse> getNotifications(Long userId) {
        return notificationJpaRepository
                .findAllByUserIdOrUserIdIsNullOrderByCreatedAtDesc(userId)
                .stream()
                .map(n -> NotificationResponse.builder()
                        .id(n.getId())
                        .mensagem(n.getMensagem())
                        .tipo(n.getTipo())
                        .lida(n.getLida())
                        .createdAt(n.getCreatedAt().toString())
                        .build())
                .toList();
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationJpaRepository.findById(notificationId).ifPresent(n -> {
            n.setLida(true);
            notificationJpaRepository.save(n);
        });
    }
}