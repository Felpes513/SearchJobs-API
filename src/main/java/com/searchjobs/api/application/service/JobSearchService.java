package com.searchjobs.api.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchjobs.api.application.dto.response.JobResponse;
import com.searchjobs.api.domain.model.Job;
import com.searchjobs.api.domain.model.UserProfile;
import com.searchjobs.api.domain.model.UserSkill;
import com.searchjobs.api.domain.port.in.JobSearchUseCase;
import com.searchjobs.api.domain.port.out.AiExtractionPort;
import com.searchjobs.api.domain.port.out.JobRepository;
import com.searchjobs.api.domain.port.out.JobSearchPort;
import com.searchjobs.api.domain.port.out.UserProfileRepository;
import com.searchjobs.api.domain.port.out.UserSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobSearchService implements JobSearchUseCase {

    private final JobSearchPort jobSearchPort;
    private final JobRepository jobRepository;
    private final UserSkillRepository userSkillRepository;
    private final UserProfileRepository userProfileRepository;
    private final AiExtractionPort aiExtractionPort;
    private final ObjectMapper objectMapper;

    @Override
    @Cacheable(value = "jobs-search", key = "#userId")
    public List<JobResponse> searchJobsForUser(Long userId) {
        String cargoDesejado = userProfileRepository.findByUserId(userId)
                .map(UserProfile::getCargoDesejado)
                .orElse(null);

        if (cargoDesejado == null || cargoDesejado.isBlank()) {
            throw new IllegalArgumentException(
                    "Configure seu cargo desejado no perfil antes de buscar vagas"
            );
        }

        List<String> skills = userSkillRepository.findAllByUserId(userId)
                .stream()
                .map(UserSkill::getNomeSkill)
                .toList();

        String query = buildQuery(cargoDesejado, skills);
        System.out.println(">>> Query enviada ao JSearch: " + query);

        List<Job> jobs = jobSearchPort.search(query);
        System.out.println(">>> Total de vagas retornadas: " + jobs.size());

        if (jobs.isEmpty()) return Collections.emptyList();

        jobs.forEach(jobRepository::save);

        List<JobResponse> rankedJobs = rankJobsBySkills(jobs, skills);
        return rankedJobs;
    }

    private String buildQuery(String cargoDesejado, List<String> skills) {
        if (cargoDesejado != null && !cargoDesejado.isBlank()) {
            return cargoDesejado;
        }
        if (!skills.isEmpty()) {
            return skills.get(0);
        }
        return "Desenvolvedor Software";
    }

    private List<JobResponse> rankJobsBySkills(List<Job> jobs, List<String> skills) {
        if (skills.isEmpty()) {
            return jobs.stream().map(this::toResponse).toList();
        }

        String skillsText = String.join(", ", skills);
        String jobsText = jobs.stream()
                .map(j -> "ID: %s | Título: %s | Empresa: %s | Descrição: %s"
                        .formatted(j.getExternalId(), j.getTitulo(), j.getEmpresa(),
                                j.getDescricao() != null
                                        ? j.getDescricao().substring(0, Math.min(j.getDescricao().length(), 300))
                                        : ""))
                .reduce("", (a, b) -> a + "\n" + b);

        String prompt = """
                Você é um assistente de recrutamento. Dado um candidato com as seguintes skills:
                %s
                
                E as seguintes vagas disponíveis:
                %s
                
                Retorne APENAS um array JSON com os IDs das vagas ordenados do mais relevante para o menos relevante para este candidato.
                Exemplo: ["id1", "id2", "id3"]
                Retorne apenas o array, sem explicações, sem markdown.
                """.formatted(skillsText, jobsText);

        try {
            String response = aiExtractionPort.extractResumeData(prompt).trim()
                    .replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            List<String> rankedIds = objectMapper.readValue(response, new TypeReference<>() {});

            List<Job> ranked = rankedIds.stream()
                    .map(id -> jobs.stream()
                            .filter(j -> id.equals(j.getExternalId()))
                            .findFirst()
                            .orElse(null))
                    .filter(j -> j != null)
                    .toList();

            List<Job> notRanked = jobs.stream()
                    .filter(j -> !rankedIds.contains(j.getExternalId()))
                    .toList();

            return java.util.stream.Stream.concat(ranked.stream(), notRanked.stream())
                    .map(this::toResponse)
                    .toList();

        } catch (Exception e) {
            System.out.println(">>> Erro ao rankear vagas: " + e.getMessage());
            return jobs.stream().map(this::toResponse).toList();
        }
    }

    @CacheEvict(value = "jobs-search", key = "#userId")
    public void evictJobsCache(Long userId) {
    }

    private JobResponse toResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .externalId(job.getExternalId())
                .titulo(job.getTitulo())
                .empresa(job.getEmpresa())
                .localizacao(job.getLocalizacao())
                .modeloTrabalho(job.getModeloTrabalho())
                .senioridade(job.getSenioridade())
                .descricao(job.getDescricao())
                .salario(job.getSalario())
                .jobUrl(job.getJobUrl())
                .dataPublicacao(job.getDataPublicacao() != null
                        ? job.getDataPublicacao().toString() : null)
                .build();
    }
}