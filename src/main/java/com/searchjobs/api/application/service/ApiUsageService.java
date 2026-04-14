package com.searchjobs.api.application.service;

import com.searchjobs.api.application.dto.internal.QuotaAlertMessage;
import com.searchjobs.api.config.RabbitMQConfig;
import com.searchjobs.api.infrastructure.persistence.entity.ApiUsageJpaEntity;
import com.searchjobs.api.infrastructure.persistence.repository.ApiUsageJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ApiUsageService {

    private static final int LIMITE_TOTAL = 200;
    private static final int THRESHOLD_AVISO = 150;
    private static final int THRESHOLD_CRITICO = 190;

    private final ApiUsageJpaRepository apiUsageJpaRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public void registrarRequisicao() {
        String mesAtual = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

        ApiUsageJpaEntity usage = apiUsageJpaRepository
                .findByMesReferencia(mesAtual)
                .orElseGet(() -> ApiUsageJpaEntity.builder()
                        .mesReferencia(mesAtual)
                        .totalRequisicoes(0)
                        .build());

        usage.setTotalRequisicoes(usage.getTotalRequisicoes() + 1);
        apiUsageJpaRepository.save(usage);

        int total = usage.getTotalRequisicoes();
        int restantes = LIMITE_TOTAL - total;

        if (total == THRESHOLD_AVISO) {
            publicarAlerta("AVISO", total, restantes);
        } else if (total == THRESHOLD_CRITICO) {
            publicarAlerta("CRITICO", total, restantes);
        }
    }

    public int getTotalRequisicoes() {
        String mesAtual = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        return apiUsageJpaRepository.findByMesReferencia(mesAtual)
                .map(ApiUsageJpaEntity::getTotalRequisicoes)
                .orElse(0);
    }

    private void publicarAlerta(String tipo, int total, int restantes) {
        QuotaAlertMessage message = QuotaAlertMessage.builder()
                .totalRequisicoes(total)
                .limite(LIMITE_TOTAL)
                .restantes(restantes)
                .tipo(tipo)
                .build();

        rabbitTemplate.convertAndSend(RabbitMQConfig.QUOTA_ALERT_QUEUE, message);
    }
}