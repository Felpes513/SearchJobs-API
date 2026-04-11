package com.searchjobs.api.application.service;

import com.searchjobs.api.application.dto.request.CreateApplicationRequest;
import com.searchjobs.api.application.dto.request.UpdateApplicationStatusRequest;
import com.searchjobs.api.application.dto.response.ApplicationKanbanResponse;
import com.searchjobs.api.application.dto.response.ApplicationResponse;
import com.searchjobs.api.domain.model.Application;
import com.searchjobs.api.domain.port.in.ApplicationUseCase;
import com.searchjobs.api.domain.port.out.ApplicationRepository;
import com.searchjobs.api.domain.port.out.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService implements ApplicationUseCase {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;

    @Override
    public ApplicationKanbanResponse getKanban(Long userId) {
        List<ApplicationResponse> all = applicationRepository.findAllByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();

        return ApplicationKanbanResponse.builder()
                .pendente(filterByStatus(all, "PENDENTE"))
                .salva(filterByStatus(all, "SALVA"))
                .emFila(filterByStatus(all, "EM_FILA"))
                .candidatado(filterByStatus(all, "CANDIDATADO"))
                .emAnalise(filterByStatus(all, "EM_ANALISE"))
                .entrevista(filterByStatus(all, "ENTREVISTA"))
                .aprovado(filterByStatus(all, "APROVADO"))
                .rejeitado(filterByStatus(all, "REJEITADO"))
                .ignorado(filterByStatus(all, "IGNORADO"))
                .build();
    }

    @Override
    public void create(Long userId, CreateApplicationRequest request) {
        if (applicationRepository.existsByUserIdAndJobId(userId, request.getJobId())) {
            throw new IllegalArgumentException("Você já se candidatou a esta vaga");
        }

        jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new IllegalArgumentException("Vaga não encontrada"));

        applicationRepository.save(Application.builder()
                .userId(userId)
                .jobId(request.getJobId())
                .status("PENDENTE")
                .observacao(request.getObservacao())
                .dataCandidatura(LocalDateTime.now())
                .build());
    }

    @Override
    public void updateStatus(Long userId, Long applicationId,
                             UpdateApplicationStatusRequest request) {
        Application application = applicationRepository
                .findByIdAndUserId(applicationId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Candidatura não encontrada"));

        validateStatus(request.getStatus());

        applicationRepository.save(Application.builder()
                .id(application.getId())
                .userId(application.getUserId())
                .jobId(application.getJobId())
                .status(request.getStatus())
                .observacao(application.getObservacao())
                .dataCandidatura(application.getDataCandidatura())
                .build());
    }

    @Override
    public void delete(Long userId, Long applicationId) {
        applicationRepository.findByIdAndUserId(applicationId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Candidatura não encontrada"));
        applicationRepository.delete(applicationId);
    }

    private List<ApplicationResponse> filterByStatus(List<ApplicationResponse> list,
                                                     String status) {
        return list.stream()
                .filter(a -> status.equals(a.getStatus()))
                .toList();
    }

    private void validateStatus(String status) {
        List<String> valid = List.of(
                "PENDENTE", "SALVA", "EM_FILA", "CANDIDATADO",
                "EM_ANALISE", "ENTREVISTA", "REJEITADO", "APROVADO", "IGNORADO"
        );
        if (!valid.contains(status)) {
            throw new IllegalArgumentException("Status inválido: " + status);
        }
    }

    private ApplicationResponse toResponse(Application application) {
        return jobRepository.findById(application.getJobId())
                .map(job -> ApplicationResponse.builder()
                        .id(application.getId())
                        .jobId(job.getId())
                        .titulo(job.getTitulo())
                        .empresa(job.getEmpresa())
                        .localizacao(job.getLocalizacao())
                        .modeloTrabalho(job.getModeloTrabalho())
                        .jobUrl(job.getJobUrl())
                        .status(application.getStatus())
                        .observacao(application.getObservacao())
                        .dataCandidatura(application.getDataCandidatura() != null
                                ? application.getDataCandidatura().toString() : null)
                        .build())
                .orElse(null);
    }
}