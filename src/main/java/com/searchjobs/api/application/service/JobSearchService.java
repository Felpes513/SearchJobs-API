package com.searchjobs.api.application.service;

import com.searchjobs.api.application.dto.response.JobResponse;
import com.searchjobs.api.domain.model.Job;
import com.searchjobs.api.domain.model.UserSkill;
import com.searchjobs.api.domain.port.in.JobSearchUseCase;
import com.searchjobs.api.domain.port.out.AiExtractionPort;
import com.searchjobs.api.domain.port.out.JobRepository;
import com.searchjobs.api.domain.port.out.JobSearchPort;
import com.searchjobs.api.domain.port.out.UserProfileRepository;
import com.searchjobs.api.domain.port.out.UserSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobSearchService implements JobSearchUseCase {

    private final JobSearchPort jobSearchPort;
    private final JobRepository jobRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserSkillRepository userSkillRepository;
    private final AiExtractionPort aiExtractionPort;

    @Override
    public List<JobResponse> searchJobsForUser(Long userId) {
        List<String> skills = userSkillRepository.findAllByUserId(userId)
                .stream()
                .map(UserSkill::getNomeSkill)
                .toList();

        String query = buildQuery(userId, skills);
        List<Job> jobs = jobSearchPort.search(query);

        jobs.forEach(jobRepository::save);

        return jobs.stream().map(this::toResponse).toList();
    }

    private String buildQuery(Long userId, List<String> skills) {
        String skillsText = skills.isEmpty()
                ? "desenvolvedor software"
                : String.join(", ", skills.subList(0, Math.min(skills.size(), 5)));

        String prompt = """
            Com base nas seguintes skills de um desenvolvedor: %s
            Gere UMA query de busca de vagas em português para o Brasil.
            A query deve ser curta, objetiva, com no máximo 6 palavras.
            Retorne APENAS a query, sem explicações, sem aspas, sem pontuação.
            Exemplo: Desenvolvedor Java Spring Boot Brasil
            """.formatted(skillsText);

        String query = aiExtractionPort.extractResumeData(prompt);

        return query
                .trim()
                .replaceAll("\"", "")
                .replaceAll("\n", " ")
                .replaceAll("\r", "")
                .replaceAll("\\{", "")
                .replaceAll("\\}", "")
                .trim();
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